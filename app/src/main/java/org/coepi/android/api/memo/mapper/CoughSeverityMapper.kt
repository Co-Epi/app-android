package org.coepi.android.api.memo.mapper

import org.coepi.android.api.memo.BitList
import org.coepi.android.api.memo.toBits
import org.coepi.android.api.memo.toUNibbleBitList
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.CoughSeverity.EXISTING
import org.coepi.android.api.publicreport.CoughSeverity.NONE
import org.coepi.android.tcn.toUByte

@ExperimentalUnsignedTypes
class CoughSeverityMapper : BitMapper<CoughSeverity> {

    override val bitCount: Int = 4

    override fun toBitsUnchecked(value: CoughSeverity): BitList =
        value.toByte().toBits().toUNibbleBitList()

    override fun fromBitsUnchecked(bits: BitList): CoughSeverity =
        bits.toUByteArray().toUByte().toUserInput()

    private fun UByte.toUserInput(): CoughSeverity = when (this) {
        0.toUByte() -> NONE
        1.toUByte() -> EXISTING
        2.toUByte() -> CoughSeverity.DRY
        3.toUByte() -> CoughSeverity.WET
        else -> error("Not supported: $this")
    }

    private fun CoughSeverity.toByte(): UByte = when (this) {
        NONE -> 0
        EXISTING -> 1
        CoughSeverity.DRY -> 2
        CoughSeverity.WET -> 3
    }.toUByte()
}
