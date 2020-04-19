package org.coepi.android.ui.debug.logs

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import io.reactivex.subjects.PublishSubject
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.Clipboard
import org.coepi.android.system.EnvInfos
import org.coepi.android.system.log.CachingLog
import org.coepi.android.system.log.LogLevel
import org.coepi.android.system.log.LogLevel.D
import org.coepi.android.system.log.LogLevel.V
import org.coepi.android.system.log.LogMessage
import org.coepi.android.ui.common.UINotificationData
import org.coepi.android.ui.common.UINotificationData.Success
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.notifications.NotificationsShower

class LogsViewModel(
    logger: CachingLog,
    private val clipboard: Clipboard,
    private val envInfos: EnvInfos,
    private val uiNotifier: UINotifier
) : ViewModel() {

    private val selectedLogLevelSubject: BehaviorSubject<LogLevel> = createDefault(D)
    val selectedLogLevel: LiveData<LogLevel> = selectedLogLevelSubject
        .distinct()
        .toLiveData()

    private val logDoubleClickTrigger = PublishSubject.create<Unit>()

    private val logsObservable: Observable<List<LogMessage>> = combineLatest(
        logger.logs,
        selectedLogLevelSubject
    )
    .map { (logs, logLevel) ->
        logs.filter { entry -> entry.level >= logLevel }
    }

    val logs: LiveData<List<LogMessage>> = logsObservable.toLiveData()

    private val disposables = CompositeDisposable()

    init {
        disposables += logDoubleClickTrigger.withLatestFrom(logsObservable)
            .map { (_, logs) ->
                "${envInfos.clipboardString()}\n\n${logs.toClipboardText()}"
            }
            .subscribe {
                clipboard.putInClipboard(it)
                uiNotifier.notify(Success("Logs copied to clipboard"))
            }
    }

    fun onLogLevelSelected(logLevel: LogLevel) {
        selectedLogLevelSubject.onNext(logLevel)
    }

    fun onLogLongTap() {
        logDoubleClickTrigger.onNext(Unit)
    }

    private fun List<LogMessage>.toClipboardText(): String =
        joinToString("\n") { logMessage ->
            logMessage.level.toString() + " " + logMessage.text
        }

    private fun EnvInfos.clipboardString(): String =
        "App version: $appVersionName ($appVersionCode), " +
        "Device: $deviceName, " +
        "Android version: $osVersion"
}
