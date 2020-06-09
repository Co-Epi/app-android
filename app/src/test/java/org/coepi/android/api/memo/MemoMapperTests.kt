package org.coepi.android.api.memo

import com.google.common.truth.Truth
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.CoughSeverity.EXISTING
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime.Companion.fromValue
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
            reportTime = fromValue(1589209754L),
            earliestSymptomTime = None,
            feverSeverity = FeverSeverity.NONE,
            breathlessness = false,
            coughSeverity = CoughSeverity.NONE
        )

        val memo: Memo = mapper.toMemo(report)
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
            reportTime = fromValue(1589209754L),
            earliestSymptomTime = Some(fromValue(1589209754L)),
            feverSeverity = SERIOUS,
            breathlessness = true,
            coughSeverity = EXISTING
        )

        val memo: Memo = mapper.toMemo(report)
        val mappedReport = mapper.toReport(memo)
        Truth.assertThat(mappedReport).isEqualTo(report)
    }

    // TODO test more public report symptom combinations

}
