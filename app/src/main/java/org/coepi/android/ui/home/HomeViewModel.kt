package org.coepi.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.Observable.just
import org.coepi.android.R
import org.coepi.android.R.string.home_version
import org.coepi.android.extensions.rx.toLiveData
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
    resources: Resources
) : ViewModel() {

    val versionString: LiveData<String> =
        just(resources.getString(home_version, envInfos.appVersionString()))
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
}
