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

    override fun v(message: String) {
        log(LogMessage(V, message))
    }

    override fun d(message: String) {
        log(LogMessage(D, message))
    }

    override fun i(message: String) {
        log(LogMessage(I, message))
    }

    override fun w(message: String) {
        log(LogMessage(W, message))
    }

    override fun e(message: String) {
        log(LogMessage(E, message))
    }

    private fun log(message: LogMessage) {
        logs.add(message)
    }
}

data class LogMessage(val level: LogLevel, val text: String)

enum class LogLevel {
    V, D, I, W, E
}
