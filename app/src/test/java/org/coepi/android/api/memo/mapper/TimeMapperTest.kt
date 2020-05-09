package org.coepi.android.api.memo.mapper

import com.google.common.truth.Truth
import org.coepi.android.api.memo.BitList
import org.coepi.android.api.memo.mapper.MapperTestUtils.bits
import org.coepi.android.api.memo.toBits
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause.GROUND_OWN_PACE
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.junit.Test

@ExperimentalUnsignedTypes
class TimeMapperTest {

    @Test
    fun converts_regular_time_bits_to_time() {
        val mapper = TimeMapper()
        val time = UnixTime.fromValue(1589209754L)
        val bits = time.value.toULong().toBits()
        Truth.assertThat(mapper.fromBits(bits)).isEqualTo(time)
    }

    @Test
    fun converts_regular_time_to_bits() {
        val mapper = TimeMapper()
        val time = UnixTime.fromValue(1589209754L)
        val bits = time.value.toULong().toBits()
        Truth.assertThat(mapper.toBits(time)).isEqualTo(bits)
    }

    @Test
    fun converts_0_time_bits_to_time() {
        val mapper = TimeMapper()
        val time = UnixTime.fromValue(0L)
        val bits = time.value.toULong().toBits()
        Truth.assertThat(mapper.fromBits(bits)).isEqualTo(time)
    }

    @Test
    fun converts_far_future_time_to_bits() {
        val mapper = TimeMapper()
        val time = UnixTime.fromValue(32503680000L) // 01.01.3000
        val bits = time.value.toULong().toBits()
        Truth.assertThat(mapper.toBits(time)).isEqualTo(bits)
    }

    @Test
    fun converts_far_future_time_bits_to_time() {
        val mapper = TimeMapper()
        val time = UnixTime.fromValue(32503680000L) // 01.01.3000
        val bits = time.value.toULong().toBits()
        Truth.assertThat(mapper.fromBits(bits)).isEqualTo(time)
    }
}
