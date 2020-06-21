package org.coepi.android.system.log

class CompositeLog(private vararg val logs: Log) : Log {

    override fun setup() {
        logs.forEach { it.setup() }
    }

    override fun v(message: String, tag: LogTag?) {
        logs.forEach { it.v(message, tag) }
    }

    override fun d(message: String, tag: LogTag?) {
        logs.forEach { it.d(message, tag) }
    }

    override fun i(message: String, tag: LogTag?) {
        logs.forEach { it.i(message, tag) }
    }

    override fun w(message: String, tag: LogTag?) {
        logs.forEach { it.w(message, tag) }
    }

    override fun e(message: String, tag: LogTag?) {
        logs.forEach { it.e(message, tag) }
    }
}
