package org.coepi.android.domain.symptomflow

import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.DIARRHEA
import org.coepi.android.domain.symptomflow.SymptomId.EARLIESTSYMPTOM
import org.coepi.android.domain.symptomflow.SymptomId.FEVER
import org.coepi.android.domain.symptomflow.SymptomId.LOSS_SMELL_OR_TASTE
import org.coepi.android.domain.symptomflow.SymptomId.MUSCLE_ACHES
import org.coepi.android.domain.symptomflow.SymptomId.NONE
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.domain.symptomflow.SymptomId.RUNNY_NOSE
import org.coepi.android.domain.symptomflow.SymptomStep.BREATHLESSNESS_DESCRIPTION
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_DAYS
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_DESCRIPTION
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_TYPE
import org.coepi.android.domain.symptomflow.SymptomStep.EARLIEST_SYMPTOM_DATE
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_DAYS
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_HIGHEST_TEMPERATURE
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_TEMPERATURE_SPOT
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_TEMPERATURE_TAKEN_TODAY

class SymptomFlow(
    symptomIds: List<SymptomId>
) {
    init {
        if (symptomIds.isEmpty()) {
            error("Symptoms ids must not be empty")
        }
    }

    private val steps: List<SymptomStep> = toSteps(symptomIds)
    private var currentStep: SymptomStep = steps.first()

    val firstStep: SymptomStep get() = steps.first()

    fun previous(): SymptomStep? =
        steps.getOrNull(steps.indexOf(currentStep) - 1)?.also {
            currentStep = it
        }

    fun next(): SymptomStep? =
        steps.getOrNull(steps.indexOf(currentStep) + 1)?.also {
            currentStep = it
        }
}

private fun toSteps(symptomIds: List<SymptomId>): List<SymptomStep>
    {
        var symptomStepList: MutableList<SymptomStep> = symptomIds.flatMap { it.toSteps() } as MutableList<SymptomStep>
        symptomStepList.add(EARLIEST_SYMPTOM_DATE)
        return symptomStepList
    }


private fun SymptomId.toSteps(): List<SymptomStep> =
    when (this) {
        COUGH -> listOf(COUGH_TYPE, COUGH_DAYS, COUGH_DESCRIPTION)
        BREATHLESSNESS -> listOf(BREATHLESSNESS_DESCRIPTION)
        FEVER -> listOf(
            FEVER_DAYS, FEVER_TEMPERATURE_TAKEN_TODAY, FEVER_TEMPERATURE_SPOT,
            FEVER_HIGHEST_TEMPERATURE
        )
        MUSCLE_ACHES -> listOf()
        LOSS_SMELL_OR_TASTE -> listOf()
        DIARRHEA -> listOf()
        RUNNY_NOSE -> listOf()
        OTHER -> listOf()
        NONE -> listOf()
        EARLIESTSYMPTOM -> listOf(EARLIEST_SYMPTOM_DATE)
    }
