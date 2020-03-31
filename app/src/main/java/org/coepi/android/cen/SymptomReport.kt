package org.coepi.android.cen

import java.util.Date

data class SymptomReport(
    val reportID: Int,
    val report: String,
    var cenKeys: String,
    var timeStamp: Int
)
