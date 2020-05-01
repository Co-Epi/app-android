package org.tcncoalition.tcnclient

import kotlin.text.StringBuilder

/**
 * Converts a hex string to bytes. The string may contain whitespace for
 * readability.
 *
 * @param this@hexToBytes the hex string to be converted.
 * @return the byte[]
 */
fun String.hexToBytes(): ByteArray {
    // Strip any internal whitespace
    val s = replace(" ".toRegex(), "")

    // Now parse as hex
    val len = s.length
    require(len % 2 == 0) { "Hex string must have an even length" }
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(
            s[i],
            16
        ) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

/**
 * Converts a [ByteArray] to a hex string.
 */
fun ByteArray.bytesToHex(): String {
    val hex = StringBuilder()
    for (b in this) {
        hex.append("%02x".format(b))
    }
    return hex.toString()
}
