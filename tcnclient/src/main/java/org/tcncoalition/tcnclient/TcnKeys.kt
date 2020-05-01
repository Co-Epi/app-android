package org.tcncoalition.tcnclient

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.tcncoalition.tcnclient.crypto.*
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.max

class TcnKeys(private val context: Context) {
    private val rakFile = "rak"
    private val tckFile = "tck"
    private val sharedPreferences
        get() = EncryptedSharedPreferences
            .create(
                "esp",
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

    private val rak
        get() = readEsp(rakFile, ReportAuthorizationKey)
            ?: ReportAuthorizationKey().also { writeEsp(rakFile, it) }

    private var tck: TemporaryContactKey
        get() = readEsp(tckFile, TemporaryContactKey)
            ?: rak.initialTemporaryContactKey
        set(temporaryContactKey) = writeEsp(tckFile, temporaryContactKey)

    fun generateTcn(): ByteArray {
        val start = System.currentTimeMillis()
        val tcn = tck.temporaryContactNumber.bytes
        val newTemporaryContactKey = tck.ratchet()
        // TODO: Handle case when this is null. Generate new rak and set it up...
        if (newTemporaryContactKey != null) {
            tck = newTemporaryContactKey
        }
        val end = System.currentTimeMillis()
        Log.i(TcnKeys::class.simpleName, (end - start).toString())
        return tcn
    }

    fun createReport(memoType: MemoType, report: ByteArray): SignedReport {
        val endIndex = tck.index.short
        val minutesIn14Days = TimeUnit.DAYS.toMinutes(14)
        val periods = minutesIn14Days / 15
        val startIndex = max(0, endIndex - periods).toShort()

        return rak.createReport(
            memoType,
            report,
            startIndex.toUShort(),
            endIndex.toUShort()
        )
    }

    private fun <T> readFile(fileName: String, reader: Reader<T>): T? {
        val encryptedFile = encryptedFile(fileName)

        val bytes = encryptedFile?.openFileInput()?.readBytes() ?: return null
        if (bytes.isEmpty()) return null

        return reader.fromByteArray(bytes)
    }

    private fun <T> readEsp(key: String, reader: Reader<T>): T? {
        val value = sharedPreferences.getString(key, null)
        if (value.isNullOrEmpty()) return null

        return reader.fromByteArray(Base64.decode(value, Base64.NO_WRAP))
    }

    private fun writeFile(fileName: String, bytes: Writer) {
        encryptedFile(fileName, read = false)?.openFileOutput()?.write(bytes.toByteArray())
    }

    private fun writeEsp(key: String, writer: Writer) {
        sharedPreferences.edit()
            .putString(key, Base64.encodeToString(writer.toByteArray(), Base64.NO_WRAP))
            .apply()
    }

    private fun encryptedFile(fileName: String, read: Boolean = true): EncryptedFile? {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        val file = File(context.filesDir, fileName)
        if (read && (!file.exists() || file.length() == 0L)) return null

        return EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }

}