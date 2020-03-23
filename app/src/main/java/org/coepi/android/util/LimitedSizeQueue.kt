package org.coepi.android.util

class LimitedSizeQueue<T>(private val maxSize: Int) : ArrayList<T>() {

    override fun add(element: T): Boolean {
        val r = super.add(element)
        if (size > maxSize) {
            removeRange(0, size - maxSize - 1)
        }
        return r
    }
}
