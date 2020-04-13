package org.coepi.android.domain

import java.util.Date

data class CoEpiDate(val unixTime: Long) {

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
    "$unixTime, ${Date(unixTime * 1000)} "
