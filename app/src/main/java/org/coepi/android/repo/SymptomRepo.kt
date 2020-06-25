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
import org.coepi.core.domain.model.SymptomId.BREATHLESSNESS
import org.coepi.core.domain.model.SymptomId.COUGH
import org.coepi.core.domain.model.SymptomId.DIARRHEA
import org.coepi.core.domain.model.SymptomId.FEVER
import org.coepi.core.domain.model.SymptomId.LOSS_SMELL_OR_TASTE
import org.coepi.core.domain.model.SymptomId.MUSCLE_ACHES
import org.coepi.core.domain.model.SymptomId.NONE
import org.coepi.core.domain.model.SymptomId.OTHER
import org.coepi.core.domain.model.SymptomId.RUNNY_NOSE
import org.coepi.android.system.Resources

interface SymptomRepo {
    fun symptoms(): Single<List<Symptom>>
}

class SymptomRepoImpl(
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
