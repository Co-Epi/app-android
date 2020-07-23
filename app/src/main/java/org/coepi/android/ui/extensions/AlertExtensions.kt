package org.coepi.android.ui.extensions

import org.coepi.android.ui.extensions.ExposureDurationForUI.HoursMinutes
import org.coepi.android.ui.extensions.ExposureDurationForUI.Minutes
import org.coepi.android.ui.extensions.ExposureDurationForUI.Seconds
import org.coepi.core.domain.model.Alert

val Alert.durationSeconds: Long get() = contactEnd.value - contactStart.value

val Alert.durationForUI: ExposureDurationForUI get() = when {
    durationSeconds >= secondsInHour -> {
        val (hours, mins) = secondsToHoursMinutes(durationSeconds)
        HoursMinutes(hours, mins)
    }
    durationSeconds >= secondsInMinute -> Minutes((durationSeconds / secondsInMinute).toInt())
    else -> Seconds(durationSeconds.toInt())
}

sealed class ExposureDurationForUI {
    data class Seconds(val value: Int): ExposureDurationForUI()
    data class Minutes(val value: Int): ExposureDurationForUI()
    data class HoursMinutes(val hours: Int, val minutes: Int): ExposureDurationForUI()
}

private fun secondsToHoursMinutes(seconds: Long): HourMinutes = HourMinutes(
    (seconds / secondsInHour).toInt(),
    ((seconds % secondsInHour) / secondsInMinute).toInt()
)

private data class HourMinutes(val hours: Int, val minutes: Int)

private const val secondsInHour: Int = 3600
private const val secondsInMinute: Int = 60
