package org.coepi.android.api.request

import org.coepi.android.cen.CenKey
import org.coepi.android.cen.SymptomReport
import org.coepi.android.extensions.toBase64

data class ApiParamsCenReport(
    var report: String,
    var cenKeys: List<String>
)

fun SymptomReport.toApiParamsCenReport(keys: List<CenKey>) =
    ApiParamsCenReport(
        report = report.toBase64(),
        cenKeys = keys.map { it.key } //this has to be HEX
    )
