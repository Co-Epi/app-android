package org.coepi.android.cen

import org.coepi.android.extensions.toHex

data class Cen(val bytes: ByteArray) {
    fun toHex(): String = bytes.toHex()

    override fun toString(): String = toHex()

    override fun equals(other: Any?): Boolean =
        toString() == other.toString()
}
