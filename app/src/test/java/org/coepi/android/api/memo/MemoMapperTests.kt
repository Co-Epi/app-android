package org.coepi.android.api.memo

import com.google.common.truth.Truth
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.CoughSeverity.EXISTING
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
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

// Public report <-> memo
@ExperimentalUnsignedTypes
class MemoMapperTests {

    /**
     * Maps public report to memo and back.
     * Public report has NONE or equivalent inputs.
     */
    @Test
    fun maps_no_symptoms() {
        val mapper: MemoMapper = MemoMapperImpl()
        val report = PublicReport(
            earliestSymptomTime = None,
            feverSeverity = FeverSeverity.NONE,
            breathlessness = false,
            coughSeverity = CoughSeverity.NONE
        )

        val memo: Memo = mapper.toMemo(report, UnixTime.fromValue(1589209754L))
        val mappedReport = mapper.toReport(memo)

        Truth.assertThat(mappedReport).isEqualTo(report)
    }

    /**
     * Maps public report to memo and back.
     * Public report has an arbitrary (non-NONE) value for all the inputs.
     */
    @Test
    fun maps_all_symptoms_set_arbitrary() {
        val mapper: MemoMapper = MemoMapperImpl()

        val report = PublicReport(
            earliestSymptomTime = Some(UnixTime.fromValue(1589209754L)),
            feverSeverity = SERIOUS,
            breathlessness = true,
            coughSeverity = EXISTING
        )

        val memo: Memo = mapper.toMemo(report, UnixTime.fromValue(1589209754L))
        val mappedReport = mapper.toReport(memo)
        Truth.assertThat(mappedReport).isEqualTo(report)
    }

    // TODO test more public report symptom combinations

}
