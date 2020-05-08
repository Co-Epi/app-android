package org.coepi.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import io.reactivex.android.schedulers.AndroidSchedulers
import org.coepi.android.R.plurals
import org.coepi.android.R.string.home_version
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.EnvInfos
import org.coepi.android.system.Resources
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlerts
import org.coepi.android.ui.debug.DebugFragmentDirections
import org.coepi.android.ui.debug.DebugFragmentDirections.Companion
import org.coepi.android.ui.debug.DebugFragmentDirections.Companion.actionGlobalDebug
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.symptoms.SymptomsFragmentDirections.Companion.actionGlobalSymptomsFragment

class HomeViewModel(
    private val rootNav: RootNavigation,
    envInfos: EnvInfos,
    private val resources: Resources,
    alertsRepo: AlertsRepo
) : ViewModel() {

    val versionString: LiveData<String> =
        just(resources.getString(home_version, envInfos.appVersionString()))
            .toLiveData()

    val newAlerts: LiveData<Boolean> = alertsRepo.alerts
        .map { newAlerts(it.size) }
        .startWith(newAlerts(0))
        .observeOn(AndroidSchedulers.mainThread())
        .toLiveData()

    val title: LiveData<String> = alertsRepo.alerts
        .map { title(it.size) }
        .startWith(title(0))
        .observeOn(AndroidSchedulers.mainThread())
        .toLiveData()


    fun onCheckInClick() {
        rootNav.navigate(ToDestination(actionGlobalSymptomsFragment()))
    }

    fun onSeeAlertsClick() {
        rootNav.navigate(ToDestination(actionGlobalAlerts()))
    }

    fun onDebugClick() {
        rootNav.navigate(ToDirections(actionGlobalDebug()))
    }

    private fun EnvInfos.appVersionString() = "$appVersionName ($appVersionCode)"

    private fun title(alertsSize: Int) =
        resources.getQuantityString(plurals.home_new_exposure_alert, alertsSize)

    private fun newAlerts(alertSize: Int): Boolean {
        return alertSize > 0
    }
}
