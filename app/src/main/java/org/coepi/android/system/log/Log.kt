package org.coepi.android.system.log

val cachingLog = CachingLog()
val log: Log = CompositeLog(
    cachingLog,
    DefaultLog()
).apply { setup() }

interface Log {
    fun setup()
    fun v(message: String)
    fun d(message: String)
    fun i(message: String)
    fun w(message: String)
    fun e(message: String)
}

class DefaultLog : Log {
    private val tag = "LOGGER"

    override fun setup() {}

    override fun v(message: String) {
        android.util.Log.v(tag, message)
    }

    override fun d(message: String) {
        android.util.Log.d(tag, message)
    }

    override fun i(message: String) {
        android.util.Log.i(tag, message)
    }

    override fun w(message: String) {
        android.util.Log.w(tag, message)
    }

    override fun e(message: String) {
        android.util.Log.e(tag, message)
    }
}
