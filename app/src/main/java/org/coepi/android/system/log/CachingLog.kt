package org.coepi.android.system.log

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.BehaviorSubject.createDefault
import io.reactivex.subjects.PublishSubject
import org.coepi.android.system.log.LogLevel.D
import org.coepi.android.system.log.LogLevel.E
import org.coepi.android.system.log.LogLevel.I
import org.coepi.android.system.log.LogLevel.V
import org.coepi.android.system.log.LogLevel.W
import org.coepi.android.ui.common.LimitedSizeQueue
import java.util.Date

class CachingLog : Log {
    val logs = createDefault<LimitedSizeQueue<LogMessage>>(
        LimitedSizeQueue(1000)
    )

    private val addLogTrigger: PublishSubject<LogMessage> = PublishSubject.create()
    private val disposables = CompositeDisposable()

    init {
        disposables += addLogTrigger.withLatestFrom(logs)
            .subscribe { (logMessage, logs) ->
                this.logs.onNext(logs.apply { add(logMessage) })
            }
    }

    override fun setup() {}

    override fun v(message: String, tag: LogTag?) {
        log(LogMessage(V, addTag(tag, message)))
    }

    override fun d(message: String, tag: LogTag?) {
        log(LogMessage(D, addTag(tag, message)))
    }

    override fun i(message: String, tag: LogTag?) {
        log(LogMessage(I, addTag(tag, message)))
    }

    override fun w(message: String, tag: LogTag?) {
        log(LogMessage(W, addTag(tag, message)))
    }

    override fun e(message: String, tag: LogTag?) {
        log(LogMessage(E, addTag(tag, message)))
    }

    private fun log(message: LogMessage) {
        addLogTrigger.onNext(message)
    }

    private fun addTag(tag: LogTag?, message: String) =
        (tag?.let { "$it - " } ?: "") + message
}

data class LogMessage(val level: LogLevel, val text: String, val time: Date = Date())

enum class LogLevel(val text: String) {
    V("Verbose"),
    D("Debug"),
    I("Info"),
    W("Warn"),
    E("Error")
}
