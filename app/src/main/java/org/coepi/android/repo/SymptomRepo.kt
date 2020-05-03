package org.coepi.android.repo

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.Single.just
import org.coepi.android.cen.CenReportRepo
import org.coepi.android.cen.SymptomReport
import org.coepi.android.domain.UnixTime.Companion.now
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
import org.coepi.android.system.rx.VoidOperationState
import java.util.UUID.randomUUID

interface SymptomRepo {
    val sendReportState: Observable<VoidOperationState>

    fun symptoms(): Single<List<Symptom>>
    fun submitSymptoms(symptoms: List<Symptom>)
}

class SymptomRepoImpl(
    private val reportRepo: CenReportRepo
): SymptomRepo {

    override val sendReportState: Observable<VoidOperationState> = reportRepo.sendState.share()

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

    override fun submitSymptoms(symptoms: List<Symptom>) {
        reportRepo.send(symptoms.toReport())
    }

    private fun List<Symptom>.toReport(): SymptomReport = SymptomReport(
        id = randomUUID().toString(),
        symptoms = this,
        timestamp = now()
    )
}
