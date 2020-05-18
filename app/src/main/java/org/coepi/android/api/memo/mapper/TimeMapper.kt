package org.coepi.android.api.memo.mapper

import org.coepi.android.api.memo.BitList
import org.coepi.android.api.memo.toBits
import org.coepi.android.domain.UnixTime
import org.coepi.android.tcn.toULong

@ExperimentalUnsignedTypes
class TimeMapper : BitMapper<UnixTime> {

    override val bitCount: Int = 64

    override fun toBitsUnchecked(value: UnixTime): BitList =
        value.value.toULong().toBits()

    override fun fromBitsUnchecked(bits: BitList): UnixTime =
        UnixTime.fromValue(bits.toUByteArray().toULong().toLong())
}
