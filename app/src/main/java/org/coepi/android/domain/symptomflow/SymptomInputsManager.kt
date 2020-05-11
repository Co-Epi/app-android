package org.coepi.android.domain.symptomflow

import org.coepi.android.domain.model.Temperature
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.FEVER
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever
import org.coepi.android.system.log.log

interface SymptomInputsInitalizer {
    fun selectSymptomIds(ids: List<SymptomId>)
}

interface SymptomInputsProps {
    val inputs: SymptomInputs
    fun setCoughType(type: Cough.Type?)
    fun setCoughDays(days: Cough.Days?)
    fun setCoughStatus(status: Cough.Status?)
    fun setBreathlessnessCause(cause: Breathlessness.Cause?)
    fun setFeverDays(days: Fever.Days?)
    fun setFeverTakenTemperatureToday(taken: Boolean?)
    fun setFeverTakenTemperatureSpot(spot: Fever.TemperatureSpot?)
    fun setFeverHighestTemperatureTaken(temp: Temperature?)
}

interface SymptomInputsManager : SymptomInputsInitalizer, SymptomInputsProps {
    fun clear()
}

class SymptomInputsManagerImpl :
    SymptomInputsManager {
    override var inputs: SymptomInputs =
        SymptomInputs()
        private set

    override fun selectSymptomIds(ids: List<SymptomId>) {
        inputs = initInputs(ids)
    }

    private fun initInputs(ids: List<SymptomId>): SymptomInputs =
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

    override fun setCoughType(type: Cough.Type?) {
        val cough = inputs.cough ?: error("Cough not set")
        inputs = inputs.copy(cough = cough.copy(type = type))
    }

    override fun setCoughDays(days: Cough.Days?) {
        val cough = inputs.cough ?: error("Cough not set")
        inputs = inputs.copy(cough = cough.copy(days = days))
    }

    override fun setCoughStatus(status: Cough.Status?) {
        val cough = inputs.cough ?: error("Cough not set")
        inputs = inputs.copy(cough = cough.copy(status = status))
    }

    override fun setBreathlessnessCause(cause: Breathlessness.Cause?) {
        val breathlessness = inputs.breathlessness ?: error("Breathlessness not set")
        inputs = inputs.copy(breathlessness = breathlessness.copy(cause = cause))
    }

    override fun setFeverDays(days: Fever.Days?) {
        val fever = inputs.fever ?: error("Fever not set")
        inputs = inputs.copy(fever = fever.copy(days = days))
    }

    override fun setFeverTakenTemperatureToday(taken: Boolean?) {
        val fever = inputs.fever ?: error("Fever not set")
        inputs = inputs.copy(fever = fever.copy(takenTemperatureToday = taken))
    }

    override fun setFeverTakenTemperatureSpot(spot: Fever.TemperatureSpot?) {
        val fever = inputs.fever ?: error("Fever not set")
        inputs = inputs.copy(fever = fever.copy(temperatureSpot = spot))
    }

    override fun setFeverHighestTemperatureTaken(temp: Temperature?) {
        val fever = inputs.fever ?: error("Fever not set")
        inputs = inputs.copy(fever = fever.copy(highestTemperature = temp))
    }

    override fun clear() {
        inputs = SymptomInputs()
    }
}
