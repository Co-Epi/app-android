package org.coepi.android.api.memo

import com.google.common.truth.Truth
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.api.publicreport.PublicReportMapper
import org.coepi.android.api.publicreport.PublicReportMapperImpl
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.model.Temperature
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
import org.coepi.android.domain.symptomflow.SymptomInputs.EarliestSymptom
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Armpit
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.junit.Test

// Symptom inputs -> public report
@ExperimentalUnsignedTypes
class PublicReportMapperTests {

    @Test
    fun generates_public_report_no_inputs() {
        val inputs = SymptomInputs(setOf(NONE))

        val reportMapper: PublicReportMapper = PublicReportMapperImpl()
        val report = reportMapper.toPublicReport(inputs)

        Truth.assertThat(report).isEqualTo(PublicReport(
            earliestSymptomTime = None,
            feverSeverity = FeverSeverity.NONE,
            coughSeverity = CoughSeverity.NONE,
            breathlessness = false
        ))
    }

    @Test
    fun generates_public_report_arbitrary_inputs() {
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
                highestTemperature = Some(Temperature.Fahrenheit(99f)),
                temperatureSpot = Some(Armpit)
            ),
            earliestSymptom = EarliestSymptom(
                time = Some(UnixTime.fromValue(1589202354L))
            )
        )

        val reportMapper: PublicReportMapper = PublicReportMapperImpl()
        val report = reportMapper.toPublicReport(inputs)

        val memo: Memo = mapper.toMemo(report, UnixTime.fromValue(1589209754L))
        val mappedReport = mapper.toReport(memo)
        Truth.assertThat(report).isEqualTo(PublicReport(
            earliestSymptomTime = Some(UnixTime.fromValue(1589202354L)),
            feverSeverity = FeverSeverity.MILD,
            coughSeverity = CoughSeverity.DRY,
            breathlessness = true
        ))
    }

    // TODO test more symptom combinations
}
