package org.coepi.android.extensions

import android.os.Build.VERSION.SDK_INT
import org.coepi.android.system.log.log
import org.coepi.android.util.Base64Ext
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

fun ByteArray.toBase64String(): String =
    Base64Ext.encodeBytes(this)

fun String.toBase64(): String =
    toByteArray(UTF_8).toBase64String()

fun String.base64ToUtf8(): String? =
    base64ToByteArray()?.toString(UTF_8)

fun String.base64ToByteArray(): ByteArray? =
    try {
        Base64Ext.decode(this)
    } catch (t: Throwable) {
        log.e("Couldn't decode string with Base64: $this, api: $SDK_INT")
        null
    }
