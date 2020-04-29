package org.coepi.android.api

import org.coepi.android.cen.CenReport

data class ApiCenReport(
    val reportID: String,
    val report: String?,
    val reportTimeStamp: Long
)

fun ApiCenReport.toCenReport(): CenReport = CenReport(
    id = reportID,
    report = report ?: "", // The api omits the key if the report is empty
    timestamp = reportTimeStamp
)
