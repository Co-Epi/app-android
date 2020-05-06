package org.coepi.android.ui.symptoms

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.R.string.symptoms_success_message
import org.coepi.android.domain.model.Symptom
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.extensions.toggle
import org.coepi.android.repo.SymptomRepo
import org.coepi.android.system.Resources
import org.coepi.android.ui.thanks.ThanksFragmentDirections
import org.coepi.android.ui.thanks.ThanksFragmentDirections.Companion
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.extensions.rx.toNotification
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.thanks.ThanksFragmentDirections.Companion.actionGlobalThanksFragment
import org.coepi.android.ui.symptoms.cough.CoughTypeFragmentDirections.Companion.actionGlobalCoughTypeFragment

class SymptomsViewModel (
    private val symptomRepo: SymptomRepo,
    resources: Resources,
    uiNotifier: UINotifier,
    val navigation: RootNavigation
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomRepo.sendReportState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

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

    private val disposables = CompositeDisposable()

    private val selectedSymptoms = symptomsObservable
        .map { symptoms -> symptoms
            .filter { it.isChecked }
            .map { it.symptom }
        }

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

        disposables += symptomRepo.sendReportState
            .toNotification(resources.getString(symptoms_success_message))
            .observeOn(mainThread())
            .subscribe{
                uiNotifier.notify(it)
                navigation.navigate(ToDestination(actionGlobalThanksFragment()))
            }
    }

    fun onChecked(symptom: SymptomViewData) {
        checkedSymptomTrigger.onNext(symptom)
    }

    fun onSubmit() {
        navigation.navigate((ToDestination(actionGlobalCoughTypeFragment())))
        //submitTrigger.onNext(Unit)
    }

    fun onBack(){
        navigation.navigate(Back)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    private fun Symptom.toViewData(isChecked: Boolean): SymptomViewData =
        SymptomViewData(name, isChecked, this)
}
