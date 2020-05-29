package org.coepi.android.api.memo

import kotlin.math.ceil

/**
 * A sequence of 0 and 1s, represented as booleans.
 * @param bits Less significant bit (LSB) comes first (not to be confused with byte endianness).
 * E.g. 3 bits 110 is bits[0] -> false, bits[1] -> true, bits[2] -> true.
 */
@ExperimentalUnsignedTypes
data class BitList(val bits: List<Boolean>) {
    fun concat(bitList: BitList): BitList =
        BitList(bits + bitList.bits)

    fun toUByteArray(): UByteArray =
        bits.asByteChunks()
            .map { chunk ->
                // Convert bit arrays to bytes using strings. This is not efficient but it's
                // unproblematic for the report memo, which is not generated frequently and
                // has a small size.
                chunk.toBitString()
                    .reversed() // toUByte expects most significant bit first
                    .toUByte(2)
            }
            .toUByteArray()

    private fun List<Boolean>.asByteChunks(): List<List<Boolean>> =
        fillUntilSize(ceil(size / 8.0).toInt() * 8, false)
        .chunked(8)

    private fun List<Boolean>.toBitString() =
        joinToString(separator = "") { if (it) "1" else "0" }

    fun size(): Int = bits.size

    override fun toString(): String =
        bits
//            .reversed() // Show most significant bits left
            .asByteChunks()
            .joinToString(separator = " ") { it.toBitString() }
}

@ExperimentalUnsignedTypes
fun BitList.toUNibbleBitList(): BitList =
    BitList(bits.fillUntilSize(4, false).take(4))

private fun <T> List<T>.fillUntilSize(size: Int, with: T): List<T> {
    val mutableList = toMutableList()
    while (mutableList.size < size) {
        mutableList.add(with)
    }
    return mutableList.toList()
}
