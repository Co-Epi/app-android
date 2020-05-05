package org.coepi.android.repo.reportsupdate

import org.coepi.android.domain.UnixTime

// TODO maybe rename in something more generic
data class ReportsInterval(val number: Long, val length: Long) {
    init {
        if (length <= 0) {
            throw IllegalArgumentException("Invalid interval length: $length")
        }
    }

    fun next(): ReportsInterval = ReportsInterval(
        number + 1,
        length
    )

    val start: Long = number * length
    val end: Long = start + length

    fun startsBefore(time: UnixTime): Boolean = start < time.value

    fun endsBefore(time: UnixTime): Boolean = end < time.value

    companion object {
        fun createFor(time: UnixTime, lengthSeconds: Long = 21600L): ReportsInterval =
            ReportsInterval(
                number = time.value / lengthSeconds,
                length = lengthSeconds
            )
    }
}
