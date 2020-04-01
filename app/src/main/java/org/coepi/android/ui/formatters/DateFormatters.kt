package org.coepi.android.ui.formatters

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date

object DateFormatters {
    val dotFormatter = DotDateFormatter()
}

class DotDateFormatter {
    @SuppressLint("SimpleDateFormat")
    val format = SimpleDateFormat("dd.MM.yyyy")
    fun format(date: Date): String = format.format(date)
}
