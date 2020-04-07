package org.coepi.android.ui.symptoms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.R
import org.coepi.android.R.string.symptoms_success_message
import org.coepi.android.domain.model.Symptom
import org.coepi.android.extensions.toLiveData
import org.coepi.android.extensions.toggle
import org.coepi.android.repo.SymptomRepo
import org.coepi.android.system.Resources
import org.coepi.android.ui.common.UINotificationData
import org.coepi.android.ui.extensions.rx.toNotification
import org.coepi.android.ui.extensions.rx.toIsInProgress

class SymptomsViewModel(
    private val symptomRepo: SymptomRepo,
    resources: Resources
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomRepo.sendReportState
        .toIsInProgress().toLiveData()

    val notification: LiveData<UINotificationData> = symptomRepo.sendReportState
        .toNotification(resources.getString(symptoms_success_message)).toLiveData()

    private val selectedSymptomIds: BehaviorSubject<Set<String>> =
        BehaviorSubject.createDefault(emptySet())

    private val checkedSymptomTrigger: PublishSubject<SymptomViewData> = create()
    private val submitTrigger: PublishSubject<Unit> = create()

    private val symptomsObservable: Observable<List<SymptomViewData>> = Observables
        .combineLatest(symptomRepo.symptoms().toObservable(), selectedSymptomIds)
        .map { (symptoms, selectedIds) ->
            symptoms.map { it.toViewData(isChecked = selectedIds.contains(it.id)) }
        }

    val symptoms: LiveData<List<SymptomViewData>> = symptomsObservable.toLiveData()

    private val selectedSymptoms = symptomsObservable
        .map { symptoms -> symptoms
            .filter { it.isChecked }
            .map { it.symptom }
        }

    private val disposables = CompositeDisposable()

    init {
        disposables += checkedSymptomTrigger
            .withLatestFrom(selectedSymptomIds)
            .subscribe { (selectedSymptom, selectedIds) ->
                selectedSymptomIds.onNext(selectedIds.toggle(selectedSymptom.symptom.id))
        }

        disposables += submitTrigger
            .withLatestFrom(selectedSymptoms)
            .subscribe{ (_, symptoms) ->
                symptomRepo.submitSymptoms(symptoms)
            }
    }

    fun onChecked(symptom: SymptomViewData) {
        checkedSymptomTrigger.onNext(symptom)
    }

    fun onSubmit() {
        submitTrigger.onNext(Unit)
    }

    private fun Symptom.toViewData(isChecked: Boolean): SymptomViewData =
        SymptomViewData(name, isChecked, this)
}
