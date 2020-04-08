package org.coepi.android.ui.debug.logs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.log.CachingLog
import org.coepi.android.system.log.LogLevel
import org.coepi.android.system.log.LogLevel.V
import org.coepi.android.system.log.LogMessage
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.debug.logs.LogsFragmentDirections.Companion.actionGlobalCENFragment
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation

class LogsViewModel(
    logger: CachingLog,
    private val rootNav: RootNavigation
) : ViewModel() {

    private val selectedLogLevelSubject: BehaviorSubject<LogLevel> = createDefault(V)

    val logs: LiveData<List<LogMessage>> =
        Observables.combineLatest(
            just<List<LogMessage>>(logger.logs),
            selectedLogLevelSubject
        )
        .map { (logs, logLevel) ->
            logs.filter { entry -> entry.level >= logLevel }
        }
        .toLiveData()

    fun onCloseClick() {
        rootNav.navigate(Back)
    }

    fun onLogLevelSelected(logLevel: LogLevel) {
        selectedLogLevelSubject.onNext(logLevel)
    }

    fun onBLEClick(){
        rootNav.navigate(ToDestination(actionGlobalCENFragment()))
    }
}
