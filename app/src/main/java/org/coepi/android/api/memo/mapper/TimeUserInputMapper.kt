package org.coepi.android.api.memo.mapper

import org.coepi.android.api.memo.BitList
import org.coepi.android.api.memo.toBits
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.UserInput
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.tcn.toULong

/**
 * Maps time that entered as user input, i.e. which needs to reserve a value for "no input"
 * We'll use ULong max to represent "no input", which corresponds to the date 12.04.292277026596
 * or invalid (corresponding to -1 timestamp) if converted to Long.
 */
@ExperimentalUnsignedTypes
class TimeUserInputMapper : BitMapper<UserInput<UnixTime>> {

    override val bitCount: Int = 64

    override fun toBitsUnchecked(value: UserInput<UnixTime>): BitList = when (value) {
        is None -> ULong.MAX_VALUE
        is Some -> value.value.value.toULong()
    }.toBits()

    override fun fromBitsUnchecked(bits: BitList): UserInput<UnixTime> =
        bits.toUByteArray().toULong().let { uLong -> when (uLong) {
            ULong.MAX_VALUE -> None
            else -> Some(UnixTime.fromValue(uLong.toLong()))
        }}
}
