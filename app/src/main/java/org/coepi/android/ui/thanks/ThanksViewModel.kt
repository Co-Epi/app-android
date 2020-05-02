package org.coepi.android.ui.thanks

import androidx.lifecycle.ViewModel
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalHomeFragment
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlerts
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.symptoms.SymptomsFragmentDirections.Companion.actionGlobalSymptomsFragment

class ThanksViewModel(
    private val navigation: RootNavigation
) : ViewModel() {

    fun onCheckInClick() {
        navigation.navigate(ToDestination(actionGlobalSymptomsFragment()))
    }

    fun onSeeAlertsClick() {
        navigation.navigate(ToDestination(actionGlobalAlerts()))
    }

    fun onCloseClick() {
        navigation.navigate(ToDestination(actionGlobalHomeFragment()))
    }

}
