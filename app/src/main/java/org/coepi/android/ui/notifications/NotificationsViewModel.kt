package org.coepi.android.ui.notifications

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
import org.coepi.android.repo.NotificationsRepo
import org.coepi.android.repo.SymptomRepo
import org.coepi.android.system.log.log
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.symptoms.SymptomViewData

class NotificationsViewModel(private val notificationsRepo: NotificationsRepo, private val rootNav: RootNavigation ) : ViewModel() {

    val symptoms : LiveData<List<NotificationsViewData>> =  notificationsRepo.notifications().map { it.map {it.toViewData()} }.toObservable().toLiveData();


    init {
        /* notificatoin will be read only, so no chek
        disposables += checkedSymptomTrigger
            .withLatestFrom(selectedSymptomIds)
            .subscribe { (selectedSymptom, selectedIds) ->
                selectedSymptomIds.onNext(selectedIds.toggle(selectedSymptom.symptom.id))
        }\

        disposables += submitTrigger
            .withLatestFrom(selectedSymptoms)
            .switchMap { (_, selectedSymptoms) ->
                notificationsRepo.submitSymptoms(selectedSymptoms).toUnitObservable()
            }
            .subscribe()
         */
    }

    private fun Symptom.toViewData() =
        NotificationsViewData(name, this)

    fun onCloseClick() {
        android.util.Log.i("logsviewmodel", "close")
        rootNav.navigate(Back)
    }
}
