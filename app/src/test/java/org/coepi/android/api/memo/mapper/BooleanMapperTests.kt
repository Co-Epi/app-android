package org.coepi.android.api.memo.mapper

import com.google.common.truth.Truth
import org.coepi.android.api.memo.BitList
import org.junit.Test

@ExperimentalUnsignedTypes
class BooleanMapperTests {

    @Test
    fun converts_0_to_false() {
        val bitlist = BitList(listOf(false))
        val mapper = BooleanMapper()
        Truth.assertThat(mapper.fromBits(bitlist)).isEqualTo(false)
    }

    @Test
    fun converts_1_to_true() {
        val bitlist = BitList(listOf(true))
        val mapper = BooleanMapper()
        Truth.assertThat(mapper.fromBits(bitlist)).isEqualTo(true)
    }

    @Test
    fun converts_false_to_0() {
        val mapper = BooleanMapper()
        Truth.assertThat(mapper.toBits(false)).isEqualTo(BitList(listOf(false)))
    }

    @Test
    fun converts_true_to_1() {
        val mapper = BooleanMapper()
        Truth.assertThat(mapper.toBits(true)).isEqualTo(BitList(listOf(true)))
    }

    @Test
    fun throws_exception_if_passed_multiple_bits() {
        val bitlist = BitList(listOf(true, false))
        val mapper = BooleanMapper()
        try {
            mapper.fromBits(bitlist)
            assert(false)
        } catch (e: IllegalStateException) {
            assert(true)
        }
    }
}
