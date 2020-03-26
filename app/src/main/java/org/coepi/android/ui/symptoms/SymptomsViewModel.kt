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
import org.coepi.android.domain.model.Symptom
import org.coepi.android.extensions.toLiveData
import org.coepi.android.extensions.toObservable
import org.coepi.android.extensions.toUnitObservable
import org.coepi.android.extensions.toggle
import org.coepi.android.repo.SymptomRepo
import org.coepi.android.system.log.log

class SymptomsViewModel(private val symptomRepo: SymptomRepo) : ViewModel() {

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
            .switchMap { (_, selectedSymptoms) ->
                symptomRepo.submitSymptoms(selectedSymptoms).toUnitObservable()
            }
            .subscribe()
    }

    fun onChecked(symptom: SymptomViewData) {
        checkedSymptomTrigger.onNext(symptom)
    }

    fun onSubmit() {
        submitTrigger.onNext(Unit)
    }

    private fun Symptom.toViewData(isChecked: Boolean) =
        SymptomViewData(name, isChecked, this)
}
