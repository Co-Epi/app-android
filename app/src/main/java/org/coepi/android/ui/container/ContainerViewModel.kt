package org.coepi.android.ui.container

import androidx.lifecycle.ViewModel
import org.coepi.android.ui.debug.logs.LogsFragmentDirections.Companion.actionGlobalLogs
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation

class ContainerViewModel(private val nav: RootNavigation) : ViewModel() {

    fun onDebugClick() {
        nav.navigate(ToDirections(actionGlobalLogs()))
    }

}
