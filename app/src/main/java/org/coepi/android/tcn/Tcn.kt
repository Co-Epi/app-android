package org.coepi.android.tcn

import org.coepi.android.extensions.toHex

data class Tcn(val bytes: ByteArray) {
    fun toHex(): String = bytes.toHex()

    override fun toString(): String = toHex()

    override fun hashCode(): Int =
        toHex().hashCode()

    override fun equals(other: Any?): Boolean =
        other is Tcn && toHex() == other.toHex()
}
