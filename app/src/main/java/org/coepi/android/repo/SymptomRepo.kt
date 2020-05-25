package org.coepi.android.repo

import io.reactivex.Single
import io.reactivex.Single.just
import org.coepi.android.R.string.symptom_report_title_breathless
import org.coepi.android.R.string.symptom_report_title_cough
import org.coepi.android.R.string.symptom_report_title_diarrhea
import org.coepi.android.R.string.symptom_report_title_fever
import org.coepi.android.R.string.symptom_report_title_loss_smell
import org.coepi.android.R.string.symptom_report_title_muscle_aches
import org.coepi.android.R.string.symptom_report_title_no_symptoms
import org.coepi.android.R.string.symptom_report_title_other
import org.coepi.android.R.string.symptom_report_title_runny_nose
import org.coepi.android.domain.model.Symptom
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.DIARRHEA
import org.coepi.android.domain.symptomflow.SymptomId.FEVER
import org.coepi.android.domain.symptomflow.SymptomId.LOSS_SMELL_OR_TASTE
import org.coepi.android.domain.symptomflow.SymptomId.MUSCLE_ACHES
import org.coepi.android.domain.symptomflow.SymptomId.NONE
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.domain.symptomflow.SymptomId.RUNNY_NOSE
import org.coepi.android.system.Resources
import org.coepi.android.tcn.TcnReportRepo

interface SymptomRepo {
    fun symptoms(): Single<List<Symptom>>
}

class SymptomRepoImpl(
    reportRepo: TcnReportRepo,
    private val resources: Resources
) : SymptomRepo {

    override fun symptoms(): Single<List<Symptom>> = just(
        listOf(
            Symptom(NONE, resources.getString(symptom_report_title_no_symptoms)),
            Symptom(COUGH, resources.getString(symptom_report_title_cough)),
            Symptom(BREATHLESSNESS, resources.getString(symptom_report_title_breathless)),
            Symptom(FEVER, resources.getString(symptom_report_title_fever)),
            Symptom(MUSCLE_ACHES, resources.getString(symptom_report_title_muscle_aches)),
            Symptom(LOSS_SMELL_OR_TASTE, resources.getString(symptom_report_title_loss_smell)),
            Symptom(DIARRHEA, resources.getString(symptom_report_title_diarrhea)),
            Symptom(RUNNY_NOSE, resources.getString(symptom_report_title_runny_nose)),
            Symptom(OTHER, resources.getString(symptom_report_title_other))
        )
    )
}
