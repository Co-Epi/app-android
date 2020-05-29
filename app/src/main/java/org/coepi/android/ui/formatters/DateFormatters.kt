package org.coepi.android.ui.formatters

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object DateFormatters {
    val dotFormatter = DotDateFormatter()
    val timeFormatter = TimeFormatter()
}

class DotDateFormatter {
    @SuppressLint("SimpleDateFormat")
    val format = SimpleDateFormat("dd.MM.yyyy")
    fun format(date: Date): String = format.format(date)
}
class TimeFormatter {
    @SuppressLint("SimpleDateFormat")
    val format = SimpleDateFormat("h:mm a")
    fun format(date: Date): String = format.format(date)
}
