package org.coepi.android.domain

import org.coepi.android.cen.Cen
import org.coepi.android.cen.CenKey
import org.coepi.android.cen.IntToByteArray
import org.coepi.android.extensions.hexToByteArray
import org.coepi.android.extensions.toHex
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

interface CenLogic {
    fun shouldGenerateNewCenKey(curTimestamp: UnixTime, cenTimestamp: UnixTime): Boolean
    fun generateCenKey(timestamp: UnixTime): CenKey

    /**
     * @param ts Unix time
     */
    fun generateCen(cenKey: CenKey, ts: Long): Cen
}

class CenLogicImpl: CenLogic {
    private val cenKeyLifetimeInSeconds = 7 * 86400 // every 7 days a new key is generated

    override fun shouldGenerateNewCenKey(curTimestamp: UnixTime, cenTimestamp: UnixTime): Boolean =
        (cenTimestamp.value == 0L) || (roundedTimestamp(curTimestamp.value) >
                roundedTimestamp(cenTimestamp.value))

    override fun generateCenKey(timestamp: UnixTime): CenKey {
        // generate a new AES Key and store it in local storage
        val secretKey = KeyGenerator.getInstance("AES")
            .apply { init(256) } // 32 bytes
            .generateKey()
        return CenKey(secretKey.encoded.toHex(), timestamp)
    }

    override fun generateCen(cenKey: CenKey, ts: Long): Cen {
        val decodedCENKey = cenKey.key.hexToByteArray()
        // rebuild secretKey using SecretKeySpec
        val secretKey: SecretKey = SecretKeySpec(decodedCENKey, 0, decodedCENKey.size, "AES")
        val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return Cen(cipher.doFinal(IntToByteArray(roundedTimestamp(ts).toInt()))) // TODO no toInt()
    }

    private fun roundedTimestamp(ts: Long): Long {
        val epoch = ts / cenKeyLifetimeInSeconds
        return epoch * cenKeyLifetimeInSeconds
    }
}
