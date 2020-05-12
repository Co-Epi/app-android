package org.coepi.android.api.memo

import com.google.common.truth.Truth
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.DIARRHEA
import org.coepi.android.domain.symptomflow.SymptomId.FEVER
import org.coepi.android.domain.symptomflow.SymptomId.LOSS_SMELL_OR_TASTE
import org.coepi.android.domain.symptomflow.SymptomId.MUSCLE_ACHES
import org.coepi.android.domain.symptomflow.SymptomId.NONE
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.domain.symptomflow.SymptomId.RUNNY_NOSE
import org.coepi.android.domain.symptomflow.SymptomInputs
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause.WALKING_YARDS_OR_MINS_ON_GROUND
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Days
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status.WORSE_WHEN_OUTSIDE
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type.DRY
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Armpit
import org.coepi.android.domain.symptomflow.Temperature
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.junit.Test

@ExperimentalUnsignedTypes
class MemoMapperTests {

    /**
     * Maps inputs to memo and back.
     * Inputs have no symptoms selected.
     */
    @Test
    fun maps_no_symptoms() {
        val mapper: MemoMapper = MemoMapperImpl()
        val inputs = SymptomInputs(setOf(NONE))
        val memo: Memo = mapper.toMemo(inputs, UnixTime.fromValue(1589209754L))
        val mappedInputs = mapper.toInputs(memo)
        Truth.assertThat(mappedInputs).isEqualTo(inputs)
    }

    /**
     * Maps inputs to memo and back.
     * Inputs have an arbitrary value for all the inputs.
     */
    @Test
    fun maps_all_symptoms_set_arbitrary() {
        val mapper: MemoMapper = MemoMapperImpl()

        val inputs = SymptomInputs(
            ids = setOf(NONE, FEVER, BREATHLESSNESS, COUGH, OTHER),
            cough = Cough(
                type = Some(DRY),
                days = Some(Days(60)),
                status = Some(WORSE_WHEN_OUTSIDE)
            ),
            breathlessness = Breathlessness(
                cause = Some(WALKING_YARDS_OR_MINS_ON_GROUND)
            ),
            fever = Fever(
                days = Some(Fever.Days(1)),
                takenTemperatureToday = Some(true),
                highestTemperature = Some(Temperature(39f)),
                temperatureSpot = Some(Armpit)
            )
        )
        val expectedInputs = SymptomInputs(
            ids = setOf(NONE, FEVER, BREATHLESSNESS, COUGH, OTHER)
        )
        val memo: Memo = mapper.toMemo(inputs, UnixTime.fromValue(1589209754L))
        val mappedInputs = mapper.toInputs(memo)
        Truth.assertThat(mappedInputs).isEqualTo(expectedInputs)
    }
}
