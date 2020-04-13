package org.coepi.android.domain

import java.util.Date

data class CoEpiDate(private val date: Date) {
    val unixTime: Long = date.time / 1000

    companion object {

        /**
         * @param timestamp Unix time (millis)
         */
        private fun fromMillis(timestamp: Long): CoEpiDate =
            CoEpiDate(Date(timestamp))

        /**
         * @param timestamp Unix time
         */
        fun fromSeconds(timestamp: Long): CoEpiDate =
            fromMillis(timestamp * 1000)

        fun minDate(): CoEpiDate =
            CoEpiDate(Date(0))

        fun now(): CoEpiDate =
            CoEpiDate(Date())
    }
}
