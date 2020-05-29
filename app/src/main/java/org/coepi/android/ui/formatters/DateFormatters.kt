package org.coepi.android.ui.formatters

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object DateFormatters {
    val dotFormatter = DotDateFormatter()
    val timeFormatter = TimeFormatter()
    val monthDayFormatter = MonthDayFormatter()
}

class DotDateFormatter {
    @SuppressLint("SimpleDateFormat")
    val formatDayMonthYear = SimpleDateFormat("dd.MM.yyyy")

    fun formatDayMonthYear(date: Date): String = formatDayMonthYear.format(date)
}

class TimeFormatter {
    @SuppressLint("SimpleDateFormat")
    val formatTime = SimpleDateFormat("h:mm a")

    fun formatTime(date: Date): String = formatTime.format(date)
}

class MonthDayFormatter {
    @SuppressLint("SimpleDateFormat")
    val formatMonthDay = SimpleDateFormat("MMM dd")

    fun formatMonthDay(date: Date): String = formatMonthDay.format(date)
}