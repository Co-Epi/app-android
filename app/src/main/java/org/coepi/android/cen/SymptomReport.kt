package org.coepi.android.cen

import java.util.Date

data class SymptomReport(
    var reportID: String,
    var report: String,
    var cenKeys: String,
    var reportTimeStamp: Int
)
