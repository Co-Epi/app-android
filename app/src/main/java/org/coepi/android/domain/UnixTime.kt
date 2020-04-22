package org.coepi.android.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
class UnixTime private constructor(val value: Long) : Parcelable {

    companion object {
        fun fromValue(value: Long): UnixTime =
            UnixTime(value)

        fun minTimestamp(): UnixTime =
            UnixTime(0)

        fun now(): UnixTime =
            UnixTime(Date().time / 1000)
    }

    override fun equals(other: Any?): Boolean =
        other is UnixTime && value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String =
        "$value, ${toDate()}"

    fun toDate() =
        Date(value * 1000)
}
