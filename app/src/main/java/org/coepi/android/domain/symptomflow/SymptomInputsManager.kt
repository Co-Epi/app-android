package org.coepi.android.domain.symptomflow

import org.coepi.android.domain.model.Temperature
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.DIARRHEA
import org.coepi.android.domain.symptomflow.SymptomId.FEVER
import org.coepi.android.domain.symptomflow.SymptomId.LOSS_SMELL_OR_TASTE
import org.coepi.android.domain.symptomflow.SymptomId.MUSCLE_ACHES
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.domain.symptomflow.SymptomId.RUNNY_NOSE
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever
import org.coepi.android.extensions.toUnixTime
import org.coepi.android.system.log.log
import org.threeten.bp.Instant
import org.threeten.bp.temporal.ChronoUnit.DAYS

interface SymptomInputsInitalizer {
    fun selectSymptomIds(ids: Set<SymptomId>)
}

interface SymptomInputsProps {
    val inputs: SymptomInputs
    fun setCoughType(type: UserInput<Cough.Type>)
    fun setCoughDays(days: UserInput<Cough.Days>)
    fun setCoughStatus(status: UserInput<Cough.Status>)
    fun setBreathlessnessCause(cause: UserInput<Breathlessness.Cause>)
    fun setFeverDays(days: UserInput<Fever.Days>)
    fun setFeverTakenTemperatureToday(taken: UserInput<Boolean>)
    fun setFeverTakenTemperatureSpot(spot: UserInput<Fever.TemperatureSpot>)
    fun setFeverHighestTemperatureTaken(temp: UserInput<Temperature>)
    fun setEarliestSymptomStartedDaysAgo(days: UserInput<Int>)
}

interface SymptomInputsManager : SymptomInputsInitalizer, SymptomInputsProps {
    fun clear()
}

class SymptomInputsManagerImpl : SymptomInputsManager {
    override var inputs: SymptomInputs =
        SymptomInputs()
        private set

    override fun selectSymptomIds(ids: Set<SymptomId>) {
        inputs = initInputs(ids).copy(ids = ids)
    }

    private fun initInputs(ids: Set<SymptomId>): SymptomInputs =
        ids.fold(SymptomInputs()) { acc, e ->
            when (e) {
                COUGH -> acc.copy(cough = Cough())
                BREATHLESSNESS -> acc.copy(breathlessness = Breathlessness())
                FEVER -> acc.copy(fever = Fever())
                else -> {
                    log.i("TODO handle inputs: $e")
                    acc
                }
            }
        }

    override fun setCoughType(type: UserInput<Cough.Type>) {
        if (!inputs.ids.contains(COUGH)) error("Cough not set")
        inputs = inputs.copy(cough = inputs.cough.copy(type = type))
    }

    override fun setCoughDays(days: UserInput<Cough.Days>) {
        if (!inputs.ids.contains(COUGH)) error("Cough not set")
        inputs = inputs.copy(cough = inputs.cough.copy(days = days))
    }

    override fun setCoughStatus(status: UserInput<Cough.Status>) {
        if (!inputs.ids.contains(COUGH)) error("Cough not set")
        inputs = inputs.copy(cough = inputs.cough.copy(status = status))
    }

    override fun setBreathlessnessCause(cause: UserInput<Breathlessness.Cause>) {
        if (!inputs.ids.contains(BREATHLESSNESS)) error("Breathlessness not set")
        inputs = inputs.copy(breathlessness = inputs.breathlessness.copy(cause = cause))
    }

    override fun setFeverDays(days: UserInput<Fever.Days>) {
        if (!inputs.ids.contains(FEVER)) error("Fever not set")
        inputs = inputs.copy(fever = inputs.fever.copy(days = days))
    }

    override fun setFeverTakenTemperatureToday(taken: UserInput<Boolean>) {
        if (!inputs.ids.contains(FEVER)) error("Fever not set")
        inputs = inputs.copy(fever = inputs.fever.copy(takenTemperatureToday = taken))
    }

    override fun setFeverTakenTemperatureSpot(spot: UserInput<Fever.TemperatureSpot>) {
        if (!inputs.ids.contains(FEVER)) error("Fever not set")
        inputs = inputs.copy(fever = inputs.fever.copy(temperatureSpot = spot))
    }

    override fun setFeverHighestTemperatureTaken(temp: UserInput<Temperature>) {
        if (!inputs.ids.contains(FEVER)) error("Fever not set")
        inputs = inputs.copy(fever = inputs.fever.copy(highestTemperature = temp))
    }

    override fun setEarliestSymptomStartedDaysAgo(days: UserInput<Int>) {
        inputs = inputs.copy(earliestSymptom = inputs.earliestSymptom.copy(time = days.map {
            // TODO unit tests for days <-> timestamp
            Instant.now().minus(it.toLong(), DAYS).toUnixTime()
        }))
    }

    override fun clear() {
        inputs = SymptomInputs()
    }
}
