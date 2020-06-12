package org.coepi.android.ui.alertsinfo

import androidx.lifecycle.ViewModel
import org.coepi.android.system.Resources
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class AlertsInfoViewModel(
    private val resources: Resources,
    private val navigation: RootNavigation
) : ViewModel() {

    fun onBack() {
        navigation.navigate(Back)
    }
}
