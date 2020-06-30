package org.coepi.android.domain.symptomflow

import org.coepi.android.domain.symptomflow.SymptomStep.BREATHLESSNESS_DESCRIPTION
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_DESCRIPTION
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_TYPE
import org.coepi.android.domain.symptomflow.SymptomStep.EARLIEST_SYMPTOM
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_HIGHEST_TEMPERATURE
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_TEMPERATURE_SPOT
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_TEMPERATURE_TAKEN_TODAY
import org.coepi.android.extensions.add
import org.coepi.android.system.log.log
import org.coepi.core.domain.model.SymptomId
import org.coepi.core.domain.model.SymptomId.BREATHLESSNESS
import org.coepi.core.domain.model.SymptomId.COUGH
import org.coepi.core.domain.model.SymptomId.DIARRHEA
import org.coepi.core.domain.model.SymptomId.FEVER
import org.coepi.core.domain.model.SymptomId.LOSS_SMELL_OR_TASTE
import org.coepi.core.domain.model.SymptomId.MUSCLE_ACHES
import org.coepi.core.domain.model.SymptomId.NONE
import org.coepi.core.domain.model.SymptomId.OTHER
import org.coepi.core.domain.model.SymptomId.RUNNY_NOSE

class SymptomFlow(private var steps: List<SymptomStep>) {
    init {
        if (steps.isEmpty()) {
            error("Symptoms steps must not be empty")
        }
    }

    var currentStep: SymptomStep = steps.first()
        private set

    fun previous(): SymptomStep? =
        steps.getOrNull(steps.indexOf(currentStep) - 1)?.also {
            currentStep = it
        }

    fun next(): SymptomStep? =
        steps.getOrNull(steps.indexOf(currentStep) + 1)?.also {
            currentStep = it
        }

    fun removeIfPresent(step: SymptomStep) {
        if (steps.indexOf(step) == steps.indexOf(currentStep)) {
            // This needs more logic, we don't need it, so forbidding it.
            error("Removing current step is not supported")
        }
        if (!steps.contains(step)) {
            log.i("Step: $step not present in steps: $steps. Ignoring.")
        }
        steps = steps.minus(step)
    }

    /**
     * Adds a step to the flow after the current one, if it's not in the flow already.
     */
    fun addUniqueStepAfterCurrent(step: SymptomStep) {
        if (steps.contains(step)) { return }

        val index = steps.indexOf(currentStep)
        if (index == -1) {
            error("Should be impossible: current step: $currentStep isn't in the steps: $steps")
        }
        steps = steps.add(index + 1, step)
    }

    companion object {
        fun create(symptomIds: List<SymptomId>): SymptomFlow? {
            if (symptomIds.isEmpty()) {
                log.w("Symptoms ids empty")
                return null
            }

            val steps = toSteps(symptomIds)
            if (steps.isEmpty()) {
                log.d("Symptoms have no steps. Not creating a flow.")
                return null
            }

            return SymptomFlow(steps)
        }
    }
}
private fun toSteps(symptomIds: List<SymptomId>): List<SymptomStep> {
    if (symptomIds.contains(NONE) && symptomIds.size > 1) {
        error("There must be no other symptoms selected when NONE is selected")
    }

    return symptomIds.flatMap { it.toInitialSteps() } + if (symptomIds != listOf(NONE)) {
        listOf(EARLIEST_SYMPTOM)
    } else {
        emptyList()
    }
}

private fun SymptomId.toInitialSteps(): List<SymptomStep> =
    when (this) {
        COUGH -> listOf(COUGH_TYPE, COUGH_DESCRIPTION)
        BREATHLESSNESS -> listOf(BREATHLESSNESS_DESCRIPTION)
        FEVER -> listOf(FEVER_TEMPERATURE_TAKEN_TODAY, FEVER_HIGHEST_TEMPERATURE, FEVER_TEMPERATURE_SPOT)
        MUSCLE_ACHES -> listOf()
        LOSS_SMELL_OR_TASTE -> listOf()
        DIARRHEA -> listOf()
        RUNNY_NOSE -> listOf()
        OTHER -> listOf()
        NONE -> listOf()
    }
