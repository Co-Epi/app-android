package org.coepi.android.extensions

fun <T>Set<T>.toggle(element: T): Set<T> =
    if (contains(element)) {
        this - element
    } else {
        this + element
    }
