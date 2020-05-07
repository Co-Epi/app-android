package org.coepi.android.ui.symptoms.cough

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.Single.just
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import org.coepi.android.domain.model.Symptom
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.Resources
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.thanks.ThanksFragmentDirections.Companion.actionGlobalThanksFragment

class CoughStatusViewModel (
    resources: Resources,
    uiNotifier: UINotifier,
    val navigation: RootNavigation
) : ViewModel() {

    private val selectedStatusIds: BehaviorSubject<Set<String>> =
        BehaviorSubject.createDefault(emptySet())

    private val statusesObservable: Observable<List<CoughStatusViewData>> = Observables
        .combineLatest(coughStatuses().toObservable(), selectedStatusIds)
        .map { (statuses, selectedIds) ->
            statuses.map { it.toViewData(isChecked = selectedIds.contains(it.id)) }
        }

    val statuses: LiveData<List<CoughStatusViewData>> = statusesObservable.toLiveData()


    fun onClickSkip() {
        navigateNextScreen()
    }

    private fun navigateNextScreen() {
        navigation.navigate(ToDestination(actionGlobalThanksFragment()))
    }

    fun onSubmit(){
        navigateNextScreen()
    }

    fun onBack() {
        navigation.navigate(Back)
    }

    fun coughStatuses() : Single<List<Symptom>> = just(listOf(
    Symptom("1", "Felt better and worse throughout the day"),
    Symptom("2", "Felt worse when I am outside"),
    Symptom("3", "Stayed the same or felt steadily worse")
    ))

    private fun Symptom.toViewData(isChecked: Boolean): CoughStatusViewData =
        CoughStatusViewData(name, isChecked, this)
}