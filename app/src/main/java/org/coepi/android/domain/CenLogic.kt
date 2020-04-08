package org.coepi.android.domain

import android.util.Base64
import org.coepi.android.cen.Cen
import org.coepi.android.cen.CenKey
import org.coepi.android.cen.IntToByteArray
import org.coepi.android.extensions.toHex
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

interface CenLogic {
    fun shouldGenerateNewCenKey(curTimestamp: Long, cenKeyTimestamp: Long): Boolean
    fun generateCenKey(timeStamp: Long): CenKey
    fun generateCen(cenKey: CenKey, ts: Long): Cen
}

class CenLogicImpl: CenLogic {
    private val cenKeyLifetimeInSeconds = 7 * 86400 // every 7 days a new key is generated

    override fun shouldGenerateNewCenKey(curTimestamp: Long, cenKeyTimestamp: Long): Boolean =
        (cenKeyTimestamp == 0L) || (roundedTimestamp(curTimestamp) > roundedTimestamp(cenKeyTimestamp))

    override fun generateCenKey(timeStamp: Long): CenKey {
        // generate a new AES Key and store it in local storage
        val secretKey = KeyGenerator.getInstance("AES").generateKey()
        return CenKey(secretKey.encoded.toHex(), timeStamp)
    }

    override fun generateCen(cenKey: CenKey, ts: Long): Cen {
        // decode the base64 encoded key
        val decodedCENKey = Base64.decode(cenKey.key, Base64.NO_WRAP)
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
