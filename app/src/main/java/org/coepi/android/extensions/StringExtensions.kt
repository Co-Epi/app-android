package org.coepi.android.extensions

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.util.Base64.DEFAULT
import android.util.Base64.NO_WRAP
import org.coepi.android.system.log.log
import java.nio.charset.Charset
import java.util.Base64
import kotlin.text.Charsets.UTF_8

fun String.hexToByteArray(): ByteArray {
    val carr = toCharArray()
    val size = carr.size
    val res = ByteArray(size / 2)
    var i = 0
    while (i < size) {
        val hex2 = "" + carr[i] + carr[i + 1]
        val byte: Byte = hex2.toLong(radix = 16).toByte()
        res[i / 2] = byte
        i += 2
    }
    return res
}

fun String.toBase64(): String =
    if (SDK_INT >= O) {
        Base64.getEncoder().encodeToString(toByteArray(UTF_8))
    } else {
        android.util.Base64.encodeToString(toByteArray(UTF_8), NO_WRAP)
    }

fun String.base64ToUtf8(): String? =
    base64ToByteArray()?.toString(Charset.forName("utf-8"))

private fun String.base64ToByteArray(): ByteArray? =
    try {
        if (SDK_INT >= O) {
            Base64.getDecoder().decode(this)
        } else {
            android.util.Base64.decode(this, DEFAULT)
        }
    } catch (t: Throwable) {
        log.e("Couldn't decode string with Base64: $this, api: $SDK_INT")
        null
    }
