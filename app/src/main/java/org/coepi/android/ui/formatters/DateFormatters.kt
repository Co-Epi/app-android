package org.coepi.android.ui.formatters

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object DateFormatters {
    val dotFormatter = DotDateFormatter()
    val hourMinuteFormatter = HourMinuteFormatter()
    val monthDayFormatter = MonthDayFormatter()
    val hourMinuteSecFormatter = HourMinuteSecFormatter()
}

class DotDateFormatter {
    @SuppressLint("SimpleDateFormat")
    val formatDayMonthYear = SimpleDateFormat("dd.MM.yyyy")

    fun formatDayMonthYear(date: Date): String = formatDayMonthYear.format(date)
}

class HourMinuteFormatter {
    @SuppressLint("SimpleDateFormat")
    val formatTime = SimpleDateFormat("h:mm a")

    fun formatTime(date: Date): String = formatTime.format(date)
}

class HourMinuteSecFormatter {
    @SuppressLint("SimpleDateFormat")
    val formatTime = SimpleDateFormat("h:mm:ss")

    fun formatTime(date: Date): String = formatTime.format(date)
}

class MonthDayFormatter {
    @SuppressLint("SimpleDateFormat")
    val formatMonthDay = SimpleDateFormat("MMM dd")

    fun formatMonthDay(date: Date): String = formatMonthDay.format(date)
}
