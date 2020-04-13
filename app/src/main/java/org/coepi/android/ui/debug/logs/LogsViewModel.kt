package org.coepi.android.ui.debug.logs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.log.CachingLog
import org.coepi.android.system.log.LogLevel
import org.coepi.android.system.log.LogLevel.V
import org.coepi.android.system.log.LogMessage

class LogsViewModel(logger: CachingLog) : ViewModel() {

    private val selectedLogLevelSubject: BehaviorSubject<LogLevel> = createDefault(V)

    val logs: LiveData<List<LogMessage>> =
        Observables.combineLatest(
            logger.logs,
            selectedLogLevelSubject
        )
        .map { (logs, logLevel) ->
            logs.filter { entry -> entry.level >= logLevel }
        }
        .toLiveData()

    fun onLogLevelSelected(logLevel: LogLevel) {
        selectedLogLevelSubject.onNext(logLevel)
    }
}
