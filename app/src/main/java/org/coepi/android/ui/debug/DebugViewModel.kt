package org.coepi.android.ui.debug

import androidx.lifecycle.ViewModel
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class DebugViewModel(
    private val rootNav: RootNavigation
) : ViewModel() {

    fun onCloseClick() {
        rootNav.navigate(Back)
    }
}
