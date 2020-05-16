package org.coepi.android.repo

import io.reactivex.Single
import io.reactivex.Single.just
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
import org.coepi.android.tcn.TcnReportRepo

interface SymptomRepo {
    fun symptoms(): Single<List<Symptom>>
}

class SymptomRepoImpl(reportRepo: TcnReportRepo): SymptomRepo {

    override fun symptoms(): Single<List<Symptom>> = just(listOf(
        Symptom(NONE, "I don\'t have any symptoms today"),
        Symptom(COUGH, "Cough"),
        Symptom(BREATHLESSNESS, "Breathless"),
        Symptom(FEVER, "Fever"),
        Symptom(MUSCLE_ACHES, "Muscle aches"),
        Symptom(LOSS_SMELL_OR_TASTE, "Loss of smell or taste"),
        Symptom(DIARRHEA, "Diarrhea"),
        Symptom(RUNNY_NOSE, "Runny nose"),
        Symptom(OTHER, "I have symptoms that are not on the list")
    ))
}
