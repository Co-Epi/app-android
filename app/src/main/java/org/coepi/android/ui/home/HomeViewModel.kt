package org.coepi.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.subjects.PublishSubject
import org.coepi.android.R.plurals
import org.coepi.android.R.string.home_contact_alerts_card_description
import org.coepi.android.R.string.home_contact_alerts_card_title
import org.coepi.android.R.string.home_my_health_card_description
import org.coepi.android.R.string.home_my_health_card_title
import org.coepi.android.R.string.home_version
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.EnvInfos
import org.coepi.android.system.Resources
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlerts
import org.coepi.android.ui.debug.DebugFragmentDirections.Companion.actionGlobalDebug
import org.coepi.android.ui.home.HomieCardId.SEE_ALERTS
import org.coepi.android.ui.home.HomeCardId.SYMPTOM_REPORTING
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.symptoms.SymptomsFragmentDirections.Companion.actionGlobalSymptomsFragment

enum class HomeCardId {
    SYMPTOM_REPORTING, SEE_ALERTS
}

class HomeViewModel(
    private val rootNav: RootNavigation,
    envInfos: EnvInfos,
    private val resources: Resources,
    alertsRepo: AlertsRepo
) : ViewModel() {

    private val newAlerts: LiveData<Boolean> = alertsRepo.alerts
        .map { it.isNotEmpty() }
        .startWith { false }
        .observeOn(mainThread())
        .toLiveData()

    private val disposables = CompositeDisposable()

    private val homeCardClickSubject: PublishSubject<HomeCard> = PublishSubject.create()

    val versionString: LiveData<String> =
        just(resources.getString(home_version, envInfos.appVersionString()))
            .toLiveData()

    val title: LiveData<String> = alertsRepo.alerts
        .map { title(it.size) }
        .startWith(title(0))
        .observeOn(mainThread())
        .toLiveData()

    val homeCardItems = listOf(
        HomeCard(
            SYMPTOM_REPORTING,
            resources.getString(home_my_health_card_title),
            resources.getString(home_my_health_card_description), newAlerts()
        ),
        HomeCard(
            SEE_ALERTS,
            resources.getString(home_contact_alerts_card_title),
            resources.getString(home_contact_alerts_card_description), newAlerts()
        )
    )

    init {
        disposables += homeCardClickSubject
            .subscribe { homeCardItem ->
                when (homeCardItem.cardId) {
                    SYMPTOM_REPORTING -> rootNav.navigate(ToDestination(actionGlobalSymptomsFragment()))
                    SEE_ALERTS -> rootNav.navigate(ToDestination(actionGlobalAlerts()))
                }
            }
    }

    fun onClicked(item: HomeCard) {
        homeCardClickSubject.onNext(item)
    }

    fun onDebugClick() {
        rootNav.navigate(ToDirections(actionGlobalDebug()))
    }

    private fun newAlerts(): Boolean {
        newAlerts.value?.let { return it }
        return false
    }

    private fun EnvInfos.appVersionString() = "$appVersionName ($appVersionCode)"

    private fun title(alertsSize: Int) =
        resources.getQuantityString(plurals.home_new_exposure_alert, alertsSize)
}
