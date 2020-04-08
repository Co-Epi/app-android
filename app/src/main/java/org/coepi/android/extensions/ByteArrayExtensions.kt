package org.coepi.android.extensions

private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()
fun ByteArray.toHex(): String {
    val hexChars = CharArray(size * 2)
    for (j in indices) {
        val v: Int = this[j].toInt() and 0xFF
        hexChars[j * 2] = HEX_ARRAY[v ushr 4]
        hexChars[j * 2 + 1] = HEX_ARRAY[v and 0x0F]
    }
    return String(hexChars)
}


public fun String.hexToByteArray(): ByteArray {
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