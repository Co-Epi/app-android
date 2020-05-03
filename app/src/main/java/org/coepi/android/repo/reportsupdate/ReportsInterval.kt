package org.coepi.android.repo.reportsupdate

import org.coepi.android.domain.UnixTime

data class ReportsInterval(val number: Long, val length: Long) {
    fun next(): ReportsInterval = ReportsInterval(
        number + 1,
        length
    )

    val start: Long = number * length
    val end: Long = start + length

    fun startsBefore(time: UnixTime): Boolean = start < time.value

    fun endsBefore(time: UnixTime): Boolean = end < time.value

    companion object {
        fun createFor(time: UnixTime): ReportsInterval {
            val intervalLengthSeconds = 21600L
            return ReportsInterval(
                number = time.value / intervalLengthSeconds,
                length = intervalLengthSeconds
            )
        }
    }
}
