package org.coepi.android.ui.common

class LimitedSizeQueue<T>(private val maxSize: Int) : ArrayList<T>() {

    override fun add(element: T): Boolean =
        super.add(element).also {
            if (size > maxSize) {
                removeRange(0, size - maxSize - 1)
            }
        }
}
