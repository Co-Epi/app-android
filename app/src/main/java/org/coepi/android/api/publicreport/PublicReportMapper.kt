package org.coepi.android.api.publicreport

import org.coepi.android.api.publicreport.FeverSeverity.MILD
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomInputs
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type.DRY
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type.WET
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some

@ExperimentalUnsignedTypes
interface PublicReportMapper {
    fun toPublicReport(inputs: SymptomInputs): PublicReport
}

@ExperimentalUnsignedTypes
class PublicReportMapperImpl : PublicReportMapper {

    override fun toPublicReport(inputs: SymptomInputs): PublicReport = PublicReport(
        reportTime = now(),
        earliestSymptomTime = inputs.earliestSymptom.time,
        feverSeverity = inputs.fever.toSeverity(),
        coughSeverity = inputs.cough.toSeverity(inputs.ids.contains(COUGH)),
        breathlessness = inputs.ids.contains(BREATHLESSNESS)
    )

    private fun Fever.toSeverity(): FeverSeverity = when(highestTemperature) {
        is None -> FeverSeverity.NONE
        is Some -> highestTemperature.value.toFarenheit().value.let { when {
            it > 100.6 -> SERIOUS
            it > 98.6 -> MILD
            it < 0 -> error("Negative number: $it") // Sanity check, since we don't handle signed numbers here.
            else -> FeverSeverity.NONE
        }}
    }

    private fun Cough.toSeverity(selectedHasCough: Boolean): CoughSeverity = when(type) {
        is None -> if (selectedHasCough) {
            CoughSeverity.EXISTING
        } else {
            CoughSeverity.NONE
        }
        is Some -> when (type.value) {
            WET -> CoughSeverity.WET
            DRY -> CoughSeverity.DRY
        }
    }
}
