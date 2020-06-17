package org.coepi.android.api.publicreport

import org.coepi.android.api.publicreport.FeverSeverity.MILD
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.DIARRHEA
import org.coepi.android.domain.symptomflow.SymptomId.LOSS_SMELL_OR_TASTE
import org.coepi.android.domain.symptomflow.SymptomId.MUSCLE_ACHES
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.domain.symptomflow.SymptomId.RUNNY_NOSE
import org.coepi.android.domain.symptomflow.SymptomInputs
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type.DRY
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type.WET
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.system.log.log

@ExperimentalUnsignedTypes
interface PublicReportMapper {
    fun toPublicReport(inputs: SymptomInputs, reportTime: UnixTime): PublicReport?
}

@ExperimentalUnsignedTypes
class PublicReportMapperImpl : PublicReportMapper {

    override fun toPublicReport(inputs: SymptomInputs, reportTime: UnixTime): PublicReport? {
        val earliestSymptomTime = inputs.earliestSymptom.time
        val feverSeverity = inputs.fever.toSeverity()
        val coughSeverity = inputs.cough.toSeverity(inputs.ids.contains(COUGH))
        val breathlessness = inputs.ids.contains(BREATHLESSNESS)
        val muscleAches = inputs.ids.contains(MUSCLE_ACHES)
        val lossSmellOrTaste = inputs.ids.contains(LOSS_SMELL_OR_TASTE)
        val diarrhea = inputs.ids.contains(DIARRHEA)
        val runnyNose = inputs.ids.contains(RUNNY_NOSE)
        val other = inputs.ids.contains(OTHER)

        return if (feverSeverity != FeverSeverity.NONE
                || coughSeverity != CoughSeverity.NONE
                || breathlessness
                || muscleAches
                || lossSmellOrTaste
                || diarrhea
                || runnyNose
                || other)
        {
            PublicReport(
                reportTime = reportTime,
                earliestSymptomTime = inputs.earliestSymptom.time,
                feverSeverity = inputs.fever.toSeverity(),
                coughSeverity = inputs.cough.toSeverity(inputs.ids.contains(COUGH)),
                breathlessness = inputs.ids.contains(BREATHLESSNESS),
                muscleAches = inputs.ids.contains(MUSCLE_ACHES),
                lossSmellOrTaste = inputs.ids.contains(LOSS_SMELL_OR_TASTE),
                diarrhea = inputs.ids.contains(DIARRHEA),
                runnyNose = inputs.ids.contains(RUNNY_NOSE),
                other = inputs.ids.contains(OTHER)
            )
        } else {
            log.i("Inputs: $inputs don't contain infos relevant to other users. Public " +
                    "report not generated.")
            null
        }
    }

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
