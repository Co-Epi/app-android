package org.coepi.android.system.log

val cachingLog = CachingLog()
val log: Log = CompositeLog(
    cachingLog,
    DefaultLog()
).apply { setup() }

// To filter logcat by multiple tags, use regex, e.g. (tag1)|(tag2)
enum class LogTag {
    BLE,  // General BLE (can't be assigned to peripheral or central)
    NET,  // Networking
    DB, // DB
    TCN_MATCHING, // Worker updating reports
    CORE,
    PERM
}

interface Log {
    fun setup()
    fun v(message: String, tag: LogTag? = null)
    fun d(message: String, tag: LogTag? = null)
    fun i(message: String, tag: LogTag? = null)
    fun w(message: String, tag: LogTag? = null)
    fun e(message: String, tag: LogTag? = null)
}

class DefaultLog : Log {
    private val tag = "LOGGER"

    override fun setup() {}

    override fun v(message: String, tag: LogTag?) {
        android.util.Log.v(tag.toString(), message)
    }

    override fun d(message: String, tag: LogTag?) {
        android.util.Log.d(tag.toString(), message)
    }

    override fun i(message: String, tag: LogTag?) {
        android.util.Log.i(tag.toString(), message)
    }

    override fun w(message: String, tag: LogTag?) {
        android.util.Log.w(tag.toString(), message)
    }

    override fun e(message: String, tag: LogTag?) {
        android.util.Log.e(tag.toString(), message)
    }

    private fun LogTag?.toString() =
        this?.toString() ?: tag
}
