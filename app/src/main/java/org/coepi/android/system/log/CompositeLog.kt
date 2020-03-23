package org.coepi.android.system.log

class CompositeLog(private vararg val logs: Log) : Log {

    override fun setup() {
        logs.forEach { it.setup() }
    }

    override fun v(message: String) {
        logs.forEach { it.v(message) }
    }

    override fun d(message: String) {
        logs.forEach { it.d(message) }
    }

    override fun i(message: String) {
        logs.forEach { it.i(message) }
    }

    override fun w(message: String) {
        logs.forEach { it.w(message) }
    }

    override fun e(message: String) {
        logs.forEach { it.e(message) }
    }
}
