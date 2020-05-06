@file:JvmName("Log")

/**
 * Mocking Android log https://stackoverflow.com/questions/36787449/how-to-mock-method-e-in-log
 * Using this exceptionally for the log, as it's inconvenient to inject it everywhere.
 */
package android.util

fun v(tag: String, msg: String): Int {
    println("VERBOSE: $msg")
    return 0
}

fun d(tag: String, msg: String): Int {
    println("DEBUG: $msg")
    return 0
}

fun i(tag: String, msg: String): Int {
    println("INFO: $msg")
    return 0
}

fun w(tag: String, msg: String): Int {
    println("WARN: $msg")
    return 0
}

fun e(tag: String, msg: String, t: Throwable): Int {
    println("ERROR: $msg")
    return 0
}

fun e(tag: String, msg: String): Int {
    return 0
}
