package org.coepi.android.api.request

import org.coepi.android.cen.CenKey
import org.coepi.android.cen.SymptomReport
import org.coepi.android.extensions.toBase64
import org.coepi.android.extensions.toHex

data class ApiParamsCenReport(
    var reportID: String,
    var report: String,
    var cenKeys: String,
    var reportTimeStamp: Long // Unix time
)

fun SymptomReport.toApiParamsCenReport(keys: List<CenKey>) =
    ApiParamsCenReport(
        reportID = id.toByteArray().toHex(),
        report = report.toBase64(),
        cenKeys = keys.joinToString(",") { it.key }, // hex
        reportTimeStamp = date.unixTime
    )
