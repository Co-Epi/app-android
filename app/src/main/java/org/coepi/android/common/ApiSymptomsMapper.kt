package org.coepi.android.common

import android.content.Context
import org.coepi.android.cen.CenReport
import org.coepi.android.cen.SymptomReport
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.model.Symptom
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.extensions.base64ToUtf8
import org.coepi.android.extensions.toBase64String
import org.coepi.android.system.Resources
import org.coepi.android.system.log.log
import org.tcncoalition.tcnclient.TcnKeys
import java.util.UUID.randomUUID
import org.coepi.android.R.string.alerts_no_symptoms_reported
import org.coepi.android.extensions.toBase64
import org.tcncoalition.tcnclient.crypto.MemoType
import java.nio.charset.StandardCharsets.UTF_8

interface ApiSymptomsMapper {
    fun toApiReport(report: SymptomReport): String
    fun fromCenReport(report: CenReport): SymptomReport
}

class ApiSymptomsMapperImpl(context: Context, private val resources: Resources) : ApiSymptomsMapper {
    private val tcnKeys: TcnKeys = TcnKeys(context)

    override fun toApiReport(report: SymptomReport): String =
        tcnKeys.createReport(
            MemoType.CoEpiV1,
            report.toMemoData()
        ).toByteArray().toBase64String()

    override fun fromCenReport(report: CenReport): SymptomReport = SymptomReport(
        id = report.id,
        symptoms = fromApiSymptomString(report.report),
        timestamp = UnixTime.fromValue(report.timestamp)
    )

    private fun List<Symptom>.toApiSymptomString(): String =
        joinToString(", ") { it.name }.toBase64()

    private fun SymptomReport.toMemoData(): ByteArray =
        symptoms.toApiSymptomString().toBase64().toByteArray(UTF_8) // TODO

    private fun fromApiSymptomString(string: String): List<Symptom> =
        if (string.isEmpty()) {
            // If no symptoms, return a symptom called "no symptoms reported"
            // This is a hack. It will be replaced soon with the 0.4 backend migration.
            // NOTE: temporarily setting id it OTHER (this method will be replaced, see note above)
            listOf(Symptom(OTHER, resources.getString(alerts_no_symptoms_reported)))
        } else {
            string.base64ToUtf8()?.split(",")?.map {
                // NOTE: temporarily setting id it OTHER (this method will be replaced, see note above)
                Symptom(OTHER, it)
            } ?: {
                log.e("Couldn't decode symptoms string: $string, returning no symptoms.")
                emptyList<Symptom>()
            }()
        }
}
