package org.coepi.android.ui.formatters

import java.text.DecimalFormat
import java.text.NumberFormat

object NumberFormatters {
    val oneDecimal: NumberFormat = DecimalFormat().apply {
        maximumFractionDigits = 1
    }
}
