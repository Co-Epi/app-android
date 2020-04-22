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
        Symptom("1", "Fever"),
        Symptom("2", "Tiredness"),
        Symptom("3", "Loss of appetite"),
        Symptom("4", "Muscle aches"),
        Symptom("5", "Trouble breathing"),
        Symptom("6", "Nasal congestion"),
        Symptom("7", "Sneezing"),
        Symptom("8", "Sore throat"),
        Symptom("9", "Headaches"),
        Symptom("10", "Diarrhea"),
        Symptom("11", "Loss of smell or taste")
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
