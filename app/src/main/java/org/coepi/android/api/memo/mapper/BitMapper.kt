package org.coepi.android.api.memo.mapper

import org.coepi.android.api.memo.BitList

@ExperimentalUnsignedTypes
interface BitMapper<T> {

    val bitCount: Int

    /**
     * Transforms to bits and checks that the count is the same as used to read.
     */
    fun toBits(value: T): BitList =
        toBitsUnchecked(value).also {
            val size = it.size()
            if (size != bitCount) {
                error("Incorrect bit count: $size. Required: $bitCount")
            }
        }

    fun fromBits(bits: BitList): T {
        if (bits.size() != bitCount) {
            error("Incorrect bit count: ${bits.size()}. Required: $bitCount")
        }
        return fromBitsUnchecked(bits)
    }

    fun toBitsUnchecked(value: T): BitList

    fun fromBitsUnchecked(bits: BitList): T
}
