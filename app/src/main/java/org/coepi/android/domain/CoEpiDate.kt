package org.coepi.android.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class CoEpiDate(val unixTime: Long) : Parcelable {

    companion object {
        fun fromUnixTime(unixTime: Long): CoEpiDate =
            CoEpiDate(unixTime)

        fun minDate(): CoEpiDate =
            CoEpiDate(0)

        fun now(): CoEpiDate =
            CoEpiDate(Date().time / 1000)
    }
}

fun CoEpiDate.debugString() =
    "$unixTime, ${Date(unixTime * 1000)}"
