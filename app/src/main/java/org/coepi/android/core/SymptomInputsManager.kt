package org.coepi.android.core

import com.google.gson.Gson
import org.coepi.android.common.Result
import org.coepi.android.domain.model.Temperature
import org.coepi.android.domain.symptomflow.SymptomId
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.DIARRHEA
import org.coepi.android.domain.symptomflow.SymptomId.FEVER
import org.coepi.android.domain.symptomflow.SymptomId.LOSS_SMELL_OR_TASTE
import org.coepi.android.domain.symptomflow.SymptomId.MUSCLE_ACHES
import org.coepi.android.domain.symptomflow.SymptomId.NONE
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.domain.symptomflow.SymptomId.RUNNY_NOSE
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause.EXERCISE
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause.GROUND_OWN_PACE
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause.HURRY_OR_HILL
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause.LEAVING_HOUSE_OR_DRESSING
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause.WALKING_YARDS_OR_MINS_ON_GROUND
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Days
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status.BETTER_AND_WORSE_THROUGH_DAY
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status.SAME_OR_STEADILY_WORSE
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status.WORSE_WHEN_OUTSIDE
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type.DRY
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type.WET
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Armpit
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Ear
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Mouth
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Other
import org.coepi.android.domain.symptomflow.UserInput
import java.io.Serializable

interface SymptomsInputManager {
    fun setSymptoms(inputs: Set<SymptomId>): Result<Unit, Throwable>
    fun setCoughType(input: UserInput<Type>): Result<Unit, Throwable>
    fun setCoughDays(input: UserInput<Days>): Result<Unit, Throwable>
    fun setCoughStatus(input: UserInput<Status>): Result<Unit, Throwable>
    fun setBreathlessnessCause(input: UserInput<Cause>): Result<Unit, Throwable>
    fun setFeverDays(input: UserInput<Fever.Days>): Result<Unit, Throwable>
    fun setFeverTakenTemperatureToday(input: UserInput<Boolean>): Result<Unit, Throwable>
    fun setFeverTakenTemperatureSpot(input: UserInput<TemperatureSpot>): Result<Unit, Throwable>
    fun setFeverHighestTemperatureTaken(input: UserInput<Temperature>): Result<Unit, Throwable>
    fun setEarliestSymptomStartedDaysAgo(input: UserInput<Int>): Result<Unit, Throwable>

    fun submitSymptoms(): Result<Unit, Throwable>
    fun clearSymptoms(): Result<Unit, Throwable>
}

class SymptomInputsManagerImpl(private val api: NativeCore, private val gson: Gson) : SymptomsInputManager {

    override fun setSymptoms(inputs: Set<SymptomId>): Result<Unit, Throwable> {
        val jniIdentifiers = inputs.map { it.toJniIdentifier() }
        return api.setSymptomIds(gson.toJson(jniIdentifiers)).asResult()
    }

    override fun setCoughType(input: UserInput<Cough.Type>): Result<Unit, Throwable> =
        api.setCoughType(
            when (input) {
                is UserInput.None -> "none"
                is UserInput.Some -> when (input.value) {
                    WET -> "wet"
                    DRY -> "dry"
                }
            }
        ).asResult()

    override fun setCoughDays(input: UserInput<Cough.Days>): Result<Unit, Throwable> =
        when (input) {
            is UserInput.None -> api.setCoughDays(0, -1)
            is UserInput.Some -> api.setCoughDays(1, input.value.value)
        }.asResult()

    override fun setCoughStatus(input: UserInput<Cough.Status>): Result<Unit, Throwable> =
        api.setCoughType(
            input.toJniStringInput { when (it) {
                BETTER_AND_WORSE_THROUGH_DAY -> "better_and_worse"
                SAME_OR_STEADILY_WORSE -> "same_steadily_worse"
                WORSE_WHEN_OUTSIDE -> "worse_outside"
            }}
        ).asResult()

    override fun setBreathlessnessCause(input: UserInput<Breathlessness.Cause>): Result<Unit, Throwable> =
        api.setCoughType(
            input.toJniStringInput { when (it) {
                EXERCISE -> "exercise"
                LEAVING_HOUSE_OR_DRESSING -> "leaving_house_or_dressing"
                WALKING_YARDS_OR_MINS_ON_GROUND -> "walking_yards_or_mins_on_ground"
                GROUND_OWN_PACE -> "ground_own_pace"
                HURRY_OR_HILL -> "hurry_or_hill"
            }}
        ).asResult()

    override fun setFeverDays(input: UserInput<Fever.Days>): Result<Unit, Throwable> =
        input.toJniIntInput { it.value }.let {
            api.setFeverDays(it.isSet, it.value).asResult()
        }

    override fun setFeverTakenTemperatureToday(input: UserInput<Boolean>): Result<Unit, Throwable> =
        input.toJniIntInput { if (it) 1 else 0 }.let {
            api.setFeverTakenTemperatureToday(it.isSet, it.value).asResult()
        }

    override fun setFeverTakenTemperatureSpot(input: UserInput<Fever.TemperatureSpot>): Result<Unit, Throwable> =
        api.setCoughType(
            input.toJniStringInput { when (it) {
                is Armpit -> "armpit"
                is Mouth -> "mouth"
                is Ear -> "ear"
                is Other -> "other"
            }}
        ).asResult()

    override fun setFeverHighestTemperatureTaken(input: UserInput<Temperature>): Result<Unit, Throwable> =
        input.toJniFloatInput { it.toFarenheit().value }.let {
            api.setFeverHighestTemperatureTaken(it.isSet, it.value).asResult()
        }

    override fun setEarliestSymptomStartedDaysAgo(input: UserInput<Int>): Result<Unit, Throwable> =
        input.toJniIntInput { it }.let {
            api.setEarliestSymptomStartedDaysAgo(it.isSet, it.value).asResult()
        }

    override fun submitSymptoms(): Result<Unit, Throwable> = api.submitSymptoms().asResult()

    override fun clearSymptoms(): Result<Unit, Throwable> = api.clearSymptoms().asResult()

    //endregion

    private fun <T : Serializable> UserInput<T>.toJniStringInput(f: (T) -> String): String =
        when (this) {
            is UserInput.None -> "none"
            is UserInput.Some -> f(value)
        }

    private fun <T : Serializable> UserInput<T>.toJniIntInput(f: (T) -> Int): JniIntInput =
        when (this) {
            is UserInput.Some -> JniIntInput(1, f(value))
            is UserInput.None -> JniIntInput(0, -1)
        }

    private fun <T : Serializable> UserInput<T>.toJniFloatInput(f: (T) -> Float): JniFloatInput =
        when (this) {
            is UserInput.Some -> JniFloatInput(1, f(value))
            is UserInput.None -> JniFloatInput(0, -1f)
        }

    private data class JniIntInput(val isSet: Int, val value: Int)
    private data class JniFloatInput(val isSet: Int, val value: Float)

    private fun Boolean.asInt(): Int = if (this) 1 else 0

    private fun SymptomId.toJniIdentifier(): String = when (this) {
        COUGH -> "cough"
        BREATHLESSNESS -> "breathlessness"
        FEVER -> "fever"
        MUSCLE_ACHES -> "muscle_aches"
        LOSS_SMELL_OR_TASTE -> "loss_smell_or_taste"
        DIARRHEA -> "diarrhea"
        RUNNY_NOSE -> "runny_nose"
        OTHER -> "other"
        NONE -> "none"
    }
}
