package org.coepi.android.extensions

fun <T> List<T>.add(index: Int, element: T): List<T> =
    toMutableList().apply {
        add(index, element)
    }
