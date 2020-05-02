package org.coepi.android.repo

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.Single.just
import org.coepi.android.cen.SymptomReport
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.domain.model.Symptom
import java.util.UUID.randomUUID

interface SymptomRepo {
    val sendReportState: Observable<VoidOperationState>

    fun symptoms(): Single<List<Symptom>>
    fun submitSymptoms(symptoms: List<Symptom>)
}

class SymptomRepoImpl(
    private val coEpiRepo: CoEpiRepo
): SymptomRepo {

    override val sendReportState: Observable<VoidOperationState> = coEpiRepo.sendReportState.share()

    override fun symptoms(): Single<List<Symptom>> = just(listOf(
        Symptom("1", "I don\'t have any symptoms today"),
        Symptom("2", "Cough"),
        Symptom("3", "Breathless"),
        Symptom("4", "Fever"),
        Symptom("5", "Muscle aches"),
        Symptom("6", "Loss of smell or taste"),
        Symptom("7", "Diarrhea"),
        Symptom("8", "Runny nose"),
        Symptom("9", "I have symptoms that are not on the list")
    ))

    override fun submitSymptoms(symptoms: List<Symptom>) {
        coEpiRepo.sendReport(symptoms.toReport())
    }

    private fun List<Symptom>.toReport(): SymptomReport = SymptomReport(
        id = randomUUID().toString(),
        symptoms = this,
        timestamp = now()
    )
}
