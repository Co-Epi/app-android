package org.coepi.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlerts
import org.coepi.android.ui.debug.DebugFragmentDirections
import org.coepi.android.ui.debug.DebugFragmentDirections.Companion
import org.coepi.android.ui.debug.DebugFragmentDirections.Companion.actionGlobalDebug
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.symptoms.SymptomsFragmentDirections.Companion.actionGlobalSymptomsFragment

class HomeViewModel(private val rootNav: RootNavigation) : ViewModel() {

    val text: LiveData<String> = Observable.just("TODO Home").toLiveData()

    fun onCheckInClick() {
        rootNav.navigate(ToDestination(actionGlobalSymptomsFragment()))
    }

    fun onSeeAlertsClick() {
        rootNav.navigate(ToDestination(actionGlobalAlerts()))
    }

    fun onDebugClick() {
        rootNav.navigate(ToDirections(actionGlobalDebug()))
    }
}