package org.coepi.android.common

import org.coepi.android.api.request.ApiParamsCenReport
import org.coepi.android.cen.CenKey
import org.coepi.android.cen.CenReport
import org.coepi.android.cen.SymptomReport
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.model.Symptom
import org.coepi.android.extensions.base64ToUtf8
import org.coepi.android.extensions.toBase64
import org.coepi.android.extensions.toHex
import org.coepi.android.system.log.log
import java.util.UUID.randomUUID

interface ApiSymptomsMapper {
    fun toApiReport(report: SymptomReport, keys: List<CenKey>): ApiParamsCenReport
    fun fromCenReport(report: CenReport): SymptomReport
}

class ApiSymptomsMapperImpl : ApiSymptomsMapper {

    override fun toApiReport(report: SymptomReport, keys: List<CenKey>): ApiParamsCenReport =
        ApiParamsCenReport(
            reportID = report.id.toByteArray().toHex(),
            report = report.symptoms.toApiSymptomString(),
            cenKeys = keys.joinToString(",") { it.key }, // hex
            reportTimeStamp = report.timestamp.value
        )

    override fun fromCenReport(report: CenReport): SymptomReport = SymptomReport(
        id = report.id,
        symptoms = fromApiSymptomString(report.report),
        timestamp = UnixTime.fromValue(report.timestamp)
    )

    private fun List<Symptom>.toApiSymptomString(): String =
        joinToString(",") { it.name }.toBase64()

    private fun fromApiSymptomString(string: String): List<Symptom> =
        string.base64ToUtf8()?.split(",")?.map {
            Symptom(randomUUID().toString(), it)
        } ?: {
            log.e("Couldn't decode symptoms string: $string, returning no symptoms.")
            emptyList<Symptom>()
        }()
}
