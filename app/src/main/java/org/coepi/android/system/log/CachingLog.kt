package org.coepi.android.system.log

import org.coepi.android.system.log.LogLevel.D
import org.coepi.android.system.log.LogLevel.E
import org.coepi.android.system.log.LogLevel.I
import org.coepi.android.system.log.LogLevel.V
import org.coepi.android.system.log.LogLevel.W
import org.coepi.android.util.LimitedSizeQueue

class CachingLog : Log {
    val logs = LimitedSizeQueue<LogMessage>(1000)

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
        logs.add(message)
    }

    private fun addTag(tag: LogTag?, message: String) =
        (tag?.let { "$it - " } ?: "") + message
}

data class LogMessage(val level: LogLevel, val text: String)

enum class LogLevel(val text: String) {
    V("Verbose"),
    D("Debug"),
    I("Info"),
    W("Warn"),
    E("Error")
}
