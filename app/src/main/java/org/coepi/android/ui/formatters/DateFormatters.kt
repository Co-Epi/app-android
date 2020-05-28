package org.coepi.android.ui.formatters

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object DateFormatters {
    val dotFormatter = DotDateFormatter()
}

class DotDateFormatter {
    @SuppressLint("SimpleDateFormat")
    val formatDayMonthYear = SimpleDateFormat("dd.MM.yyyy")
    @SuppressLint("SimpleDateFormat")
    val formatMonthDay = SimpleDateFormat("MMM dd")
    @SuppressLint("SimpleDateFormat")
    val formatTime = SimpleDateFormat("h:mm a")

    fun formatDayMonthYear(date: Date): String = formatDayMonthYear.format(date)
    fun formatMonthDay(date: Date): String = formatMonthDay.format(date)
    fun formatTime(date: Date): String = formatTime.format(date)
}
