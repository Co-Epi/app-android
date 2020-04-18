package org.coepi.android.api.request

data class ApiParamsCenReport(
    var reportID: String,
    var report: String,
    var cenKeys: String,
    var reportTimeStamp: Long // Unix time
)
