package org.coepi.android.cen

import java.util.Date

data class SymptomReport(
    val reportID: String,
    val report: String,
    var cenKeys: String,
    var reportTimeStamp: Int
)
