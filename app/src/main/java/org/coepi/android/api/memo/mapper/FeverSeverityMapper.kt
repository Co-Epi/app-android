package org.coepi.android.api.memo.mapper

import org.coepi.android.api.memo.BitList
import org.coepi.android.api.memo.toBits
import org.coepi.android.api.memo.toUNibbleBitList
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.FeverSeverity.MILD
import org.coepi.android.api.publicreport.FeverSeverity.NONE
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.tcn.toUByte

@ExperimentalUnsignedTypes
class FeverSeverityMapper : BitMapper<FeverSeverity> {

    override val bitCount: Int = 4

    override fun toBitsUnchecked(value: FeverSeverity): BitList =
        value.toByte().toBits().toUNibbleBitList()

    override fun fromBitsUnchecked(bits: BitList): FeverSeverity =
        bits.toUByteArray().toUByte().toValue()

    private fun UByte.toValue(): FeverSeverity = when (this) {
        0.toUByte() -> NONE
        1.toUByte() -> MILD
        2.toUByte() -> SERIOUS
        else -> error("Not supported: $this")
    }

    private fun FeverSeverity.toByte(): UByte = when (this) {
        NONE -> 0
        MILD -> 1
        SERIOUS -> 2
    }.toUByte()
}
