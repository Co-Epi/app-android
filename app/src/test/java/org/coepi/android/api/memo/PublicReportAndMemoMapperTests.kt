package org.coepi.android.api.memo

import com.google.common.truth.Truth
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.api.publicreport.PublicReportMapper
import org.coepi.android.api.publicreport.PublicReportMapperImpl
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.model.Temperature.Celsius
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.FEVER
import org.coepi.android.domain.symptomflow.SymptomId.NONE
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.domain.symptomflow.SymptomInputs
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause.WALKING_YARDS_OR_MINS_ON_GROUND
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Days
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status.WORSE_WHEN_OUTSIDE
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Type.DRY
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Armpit
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.junit.Test

// (Integration tests) Symptom inputs -> public report <-> Memo
@ExperimentalUnsignedTypes
class PublicReportAndMemoMapperTests {
    /**
     * Maps inputs to memo and back.
     * Inputs have no symptoms selected.
     */
    @Test
    fun maps_no_symptoms() {
        val mapper: MemoMapper = MemoMapperImpl()
        val inputs = SymptomInputs(setOf(NONE))

        val reportMapper: PublicReportMapper = PublicReportMapperImpl()
        val report = reportMapper.toPublicReport(inputs,
            UnixTime.fromValue(1589209754L))

        val memo: Memo = mapper.toMemo(report)
        val mappedReport = mapper.toReport(memo)

        Truth.assertThat(mappedReport).isEqualTo(report)
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
                highestTemperature = Some(Celsius(39f)),
                temperatureSpot = Some(Armpit)
            )
        )

        val reportMapper: PublicReportMapper = PublicReportMapperImpl()
        val report = reportMapper.toPublicReport(inputs,
            UnixTime.fromValue(1589209754L))

        val memo: Memo = mapper.toMemo(report)
        val mappedReport = mapper.toReport(memo)
        Truth.assertThat(mappedReport).isEqualTo(report)
    }
}
