package org.coepi.android.api.memo.mapper

import org.coepi.android.api.memo.BitList
import org.coepi.android.api.memo.toBits
import org.coepi.android.tcn.toULong
import org.coepi.android.tcn.toUShort
import java.nio.ByteBuffer

@ExperimentalUnsignedTypes
class VersionMapper: BitMapper<UShort> {

    override val bitCount: Int = 16

    override fun toBitsUnchecked(value: UShort): BitList =
        value.toBits()

    override fun fromBitsUnchecked(bits: BitList): UShort =
        bits.toUByteArray().toUShort()
}
