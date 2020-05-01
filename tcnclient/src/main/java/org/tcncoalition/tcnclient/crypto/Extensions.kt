package org.tcncoalition.tcnclient.crypto

import java.nio.ByteBuffer

interface Reader<T> {
    fun fromByteBuffer(buf: ByteBuffer): T

    fun fromByteArray(bytes: ByteArray): T {
        return fromByteBuffer(ByteBuffer.wrap(bytes))
    }
}

interface Writer {
    fun sizeHint(): Int

    fun toByteBuffer(buf: ByteBuffer)

    fun toByteArray(): ByteArray {
        val buf = ByteBuffer.allocate(sizeHint())
        toByteBuffer(buf)
        return buf.array()
    }
}

/** Convenience function to read a 32-byte array. */
internal fun ByteBuffer.read32(): ByteArray {
    val ret = ByteArray(32)
    get(ret)
    return ret
}

/** Convenience function to read a 64-byte array. */
internal fun ByteBuffer.read64(): ByteArray {
    val ret = ByteArray(64)
    get(ret)
    return ret
}

/** Convenience function to read a short vector with a 1-byte length tag. */
internal fun ByteBuffer.readCompactVec(): ByteArray {
    val len = get().toInt()
    val ret = ByteArray(len)
    get(ret)
    return ret
}
