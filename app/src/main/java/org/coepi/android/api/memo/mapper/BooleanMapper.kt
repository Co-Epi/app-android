package org.coepi.android.api.memo.mapper

import org.coepi.android.api.memo.BitList
import org.coepi.android.api.memo.toBits

@ExperimentalUnsignedTypes
class BooleanMapper : BitMapper<Boolean> {

    override val bitCount: Int = 1

    override fun toBitsUnchecked(value: Boolean): BitList =
        value.toBits()

    override fun fromBitsUnchecked(bits: BitList): Boolean =
        bits.bits.first()
}
