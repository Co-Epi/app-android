package org.coepi.android.api.memo.mapper

import com.google.common.truth.Truth
import org.coepi.android.api.memo.BitList
import org.junit.Test

@ExperimentalUnsignedTypes
class BitListTests {

    @Test
    fun converts_0_correctly_to_bytes() {
        val bitlist = BitList(listOf(false))
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(1)
        Truth.assertThat(byteArray[0]).isEqualTo(0.toUByte())
    }

    @Test
    fun converts_0_multiple_correctly_to_bytes() {
        val bitlist =
            BitList(Array(16) { false }.toList())
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(2)
        Truth.assertThat(byteArray[0]).isEqualTo(0.toUByte())
        Truth.assertThat(byteArray[1]).isEqualTo(0.toUByte())
    }

    @Test
    fun converts_0_multiple_rounded_correctly_to_bytes() {
        val bitlist =
            BitList(Array(20) { false }.toList())
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(3)
        Truth.assertThat(byteArray[0]).isEqualTo(0.toUByte())
        Truth.assertThat(byteArray[1]).isEqualTo(0.toUByte())
        Truth.assertThat(byteArray[2]).isEqualTo(0.toUByte())
    }

    @Test
    fun converts_1_correctly_to_bytes() {
        val bitlist = BitList(listOf(true))
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(1)
        Truth.assertThat(byteArray[0]).isEqualTo(1.toUByte())
    }

    @Test
    fun converts_1_multiple_correctly_to_bytes() {
        val bitlist =
            BitList(listOf(true) + Array(15) { false }.toList())
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(2)
        Truth.assertThat(byteArray[0]).isEqualTo(1.toUByte())
        Truth.assertThat(byteArray[1]).isEqualTo(0.toUByte())
    }

    @Test
    fun converts_1_multiple_rounded_correctly_to_bytes() {
        val bitlist =
            BitList(listOf(true) + Array(19) { false }.toList())
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(3)
        Truth.assertThat(byteArray[0]).isEqualTo(1.toUByte())
        Truth.assertThat(byteArray[1]).isEqualTo(0.toUByte())
        Truth.assertThat(byteArray[2]).isEqualTo(0.toUByte())
    }

    @Test
    fun converts_1234_to_bytes() {
        val bits = Array(16) { false }.toMutableList()
        bits[1] = true
        bits[4] = true
        bits[6] = true
        bits[7] = true
        bits[10] = true

        val bitlist = BitList(bits)
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(2)
        Truth.assertThat(byteArray[0]).isEqualTo(210.toUByte()) // the LSB of 1234 is 210
        Truth.assertThat(byteArray[1]).isEqualTo(4.toUByte()) // MSB of 1234 is 7
    }
}
