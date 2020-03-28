package org.coepi.android.ui.debug.logs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.coepi.android.cen.CENRepo
import org.coepi.android.system.log.CachingLog
import org.coepi.android.system.log.LogMessage
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class LogsViewModel(
    private val logger: CachingLog,
    private val rootNav: RootNavigation
    //private val repo : CENRepo
) : ViewModel() {

    val logs: MutableLiveData<List<LogMessage>> by lazy {
        MutableLiveData<List<LogMessage>>()
    }

    init {
        update()

        android.util.Log.i("logsviewmodel", "init")

    }

    fun onCloseClick() {
        android.util.Log.i("logsviewmodel", "close")
        rootNav.navigate(Back)
    }

    private fun update() {
        logs.value = logger.logs
    }
}
