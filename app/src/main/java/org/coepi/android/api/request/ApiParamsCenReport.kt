package org.coepi.android.api.request

import android.util.Base64
import org.coepi.android.cen.CenKey
import org.coepi.android.cen.SymptomReport
import org.coepi.android.extensions.coEpiTimestamp
import org.coepi.android.extensions.toHex
import java.util.Date

data class ApiParamsCenReport(
    var reportID: String,
    var report: String,
    var cenKeys: String,
    var reportTimeStamp: Long
)

fun SymptomReport.toApiParamsCenReport(keys: List<CenKey>) =
    ApiParamsCenReport(
        reportID = id.toByteArray().toHex(),
        report = Base64.encodeToString(report.toByteArray(), Base64.NO_WRAP),
        cenKeys = keys.joinToString(",") { Base64.decode(it.key, Base64.NO_WRAP).toHex() },//this has to be HEX
        reportTimeStamp = Date().coEpiTimestamp()
    )
