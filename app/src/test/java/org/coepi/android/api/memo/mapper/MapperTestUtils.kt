package org.coepi.android.api.memo.mapper

object MapperTestUtils {

    fun bits(count: Int, value: Boolean = false): MutableList<Boolean> = List(count) { value }
        .toMutableList()
}
