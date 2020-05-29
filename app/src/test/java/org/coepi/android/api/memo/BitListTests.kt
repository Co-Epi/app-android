package org.coepi.android.api.memo

import com.google.common.truth.Truth
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
        val bitlist = BitList(Array(16) { false }.toList())
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(2)
        Truth.assertThat(byteArray[0]).isEqualTo(0.toUByte())
        Truth.assertThat(byteArray[1]).isEqualTo(0.toUByte())
    }

    @Test
    fun converts_0_multiple_rounded_correctly_to_bytes() {
        val bitlist = BitList(Array(20) { false }.toList())
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
        val bitlist = BitList(listOf(true) + Array(15) { false }.toList())
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(2)
        Truth.assertThat(byteArray[0]).isEqualTo(1.toUByte())
        Truth.assertThat(byteArray[1]).isEqualTo(0.toUByte())
    }

    @Test
    fun converts_1_multiple_rounded_correctly_to_bytes() {
        val bitlist = BitList(listOf(true) + Array(19) { false }.toList())
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(3)
        Truth.assertThat(byteArray[0]).isEqualTo(1.toUByte())
        Truth.assertThat(byteArray[1]).isEqualTo(0.toUByte())
        Truth.assertThat(byteArray[2]).isEqualTo(0.toUByte())
    }

    @Test
    fun converts_86055_to_bytes() {
        val bits = Array(17) { false }.toMutableList()
        bits[0] = true
        bits[1] = true
        bits[2] = true
        bits[5] = true
        bits[12] = true
        bits[14] = true
        bits[16] = true

        val bitlist = BitList(bits)
        val byteArray = bitlist.toUByteArray()
        Truth.assertThat(byteArray.size).isEqualTo(3)
        Truth.assertThat(byteArray[0]).isEqualTo(39.toUByte()) // the LSB of 1234 is 39
        Truth.assertThat(byteArray[1]).isEqualTo(80.toUByte())
        Truth.assertThat(byteArray[2]).isEqualTo(1.toUByte()) // MSB of 1234 is 1
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

    @Test
    fun converts_0_to_nibble() {
        val bitList = BitList(listOf(false))
        val nibbleBitList: BitList = bitList.toUNibbleBitList()
        Truth.assertThat(nibbleBitList.size()).isEqualTo(4)
        Truth.assertThat(nibbleBitList).isEqualTo(BitList(listOf(false, false, false, false)))
    }

    @Test
    fun converts_1_to_nibble() {
        val bitList = BitList(listOf(true))
        val nibbleBitList: BitList = bitList.toUNibbleBitList()
        Truth.assertThat(nibbleBitList.size()).isEqualTo(4)
        Truth.assertThat(nibbleBitList).isEqualTo(BitList(listOf(true, false, false, false)))
    }

    @Test
    fun converts_15_to_nibble() {
        val bitList = BitList(listOf(true, true, true, true))
        val nibbleBitList: BitList = bitList.toUNibbleBitList()
        Truth.assertThat(nibbleBitList.size()).isEqualTo(4)
        Truth.assertThat(nibbleBitList).isEqualTo(BitList(listOf(true, true, true, true)))
    }

    @Test
    fun converts_15_in_5bits_to_nibble() {
        val bitList = BitList(listOf(true, true, true, true, false))
        val nibbleBitList: BitList = bitList.toUNibbleBitList()
        Truth.assertThat(nibbleBitList.size()).isEqualTo(4)
        Truth.assertThat(nibbleBitList).isEqualTo(BitList(listOf(true, true, true, true)))
    }

    @Test
    fun truncates_31_to_nibble() {
        val bitList = BitList(listOf(true, true, true, true, true))
        val nibbleBitList: BitList = bitList.toUNibbleBitList()
        Truth.assertThat(nibbleBitList.size()).isEqualTo(4)
        Truth.assertThat(nibbleBitList).isEqualTo(BitList(listOf(true, true, true, true)))
    }

    @Test
    fun truncates_18_to_nibble() {
        val bitList = BitList(listOf(false, true, false, false, true))
        val nibbleBitList: BitList = bitList.toUNibbleBitList()
        Truth.assertThat(nibbleBitList.size()).isEqualTo(4)
        Truth.assertThat(nibbleBitList).isEqualTo(BitList(listOf(false, true, false, false)))
    }
}
