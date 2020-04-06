package org.coepi.android.api

import org.coepi.android.cen.CenReport

data class ApiCenReport(
    val reportID: String,
    val report: String,
    val reportTimeStamp: Long
)

fun ApiCenReport.toCenReport(): CenReport =
    CenReport(reportID, report, reportTimeStamp)
