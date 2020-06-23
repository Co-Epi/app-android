package org.coepi.android.core

import android.content.Context
import org.coepi.android.common.Result
import org.coepi.android.common.Result.Failure
import org.coepi.android.common.Result.Success
import org.coepi.android.system.log.LogTag.CORE
import org.coepi.android.system.log.log

class NativeCore {

    init {
        System.loadLibrary("coepi_core")
    }

    external fun bootstrapCore(
        dbPath: String, level: String, coepiOnly: Boolean, logCallback: JniLogCallback
    ): JniVoidResult

    external fun clearSymptoms(): JniVoidResult

    external fun fetchNewReports(): JniAlertsArrayResult

    external fun generateTcn(): String

    external fun recordTcn(tcn: String): JniVoidResult

    external fun setBreathlessnessCause(cause: String): JniVoidResult

    external fun setCoughDays(isSet: Int, days: Int): JniVoidResult

    external fun setCoughStatus(status: String): JniVoidResult

    external fun setCoughType(coughType: String): JniVoidResult

    external fun setEarliestSymptomStartedDaysAgo(isSet: Int, days: Int): JniVoidResult

    external fun setFeverDays(isSet: Int, days: Int): JniVoidResult

    external fun setFeverHighestTemperatureTaken(isSet: Int, temp: Float): JniVoidResult

    external fun setFeverTakenTemperatureSpot(spot: String): JniVoidResult

    external fun setFeverTakenTemperatureToday(isSet: Int, taken: Int): JniVoidResult

    external fun setSymptomIds(ids: String): JniVoidResult

    external fun submitSymptoms(): JniVoidResult

    // Tests ////////////////////////////////////////////////////////////////////////

    // Basic

    external fun sendReceiveString(string: String): String

    external fun passStruct(myStruct: FFIParameterStruct): Int

    external fun returnStruct(): FFIParameterStruct

    external fun callCallback(callback: Callback): Int

    external fun registerCallback(callback: Callback): Int

    external fun triggerCallback(string: String): Int

    // Domain

    external fun testReturnAnAlert(): JniOneAlertResult

    external fun testReturnMultipleAlerts(): JniAlertsArrayResult

    /////////////////////////////////////////////////////////////////////////////////
}

data class FFIParameterStruct(
    val myInt: Int,
    val myStr: String,
    val myNested: FFINestedParameterStruct
)

data class FFINestedParameterStruct(
    val myU8: Int
)

open class Callback {
    open fun call(string: String) {
        println("callback called with: $string")
    }
}

open class JniLogCallback {
    open fun log(level: Int, message: String) {
        when (level) {
            0 -> log.v(message, CORE)
            1 -> log.d(message, CORE)
            2 -> log.i(message, CORE)
            3 -> log.w(message, CORE)
            4 -> log.e(message, CORE)
            else -> log.i("$message [annexed logger error: log level not recognized: " +
                    "$level]", CORE)
        }
    }
}

data class JniVoidResult(
    val status: Int,
    val message: String
)

data class JniOneAlertResult(
    val status: Int,
    val message: String,
    val obj: JniAlert
)

data class JniAlertsArrayResult(
    val status: Int,
    val message: String,
    val obj: Array<JniAlert>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JniAlertsArrayResult

        if (status != other.status) return false
        if (message != other.message) return false
        if (!obj.contentEquals(other.obj)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status
        result = 31 * result + message.hashCode()
        result = 31 * result + obj.contentHashCode()
        return result
    }
}

data class JniAlert(
    var id: String,
    var report: JniPublicReport,
    var contactTime: Long
)

data class JniPublicReport(
    val reportTime: Long,
    val earliestSymptomTime: Long, // -1 -> no input
    val feverSeverity: Int,
    val coughSeverity: Int,
    val breathlessness: Boolean,
    val muscleAches: Boolean,
    val lossSmellOrTaste: Boolean,
    val diarrhea: Boolean,
    val runnyNose: Boolean,
    val other: Boolean,
    val noSymptoms: Boolean
)

fun JniVoidResult.asResult(): Result<Unit, Throwable> = when (status) {
    1 -> Success(Unit)
    else -> Failure(Throwable(statusDescription()))
}

fun JniVoidResult.statusDescription(): String =
    statusDescription(status, message)

private fun statusDescription(status: Int, message: String): String =
    "Status: $status Message: $message"

fun bootstrap(applicationContext: Context) {
    val nativeApi = NativeCore()

    // getDatabasePath requires a db name, but we use need the directory
    // (to initialize multiple databases), so adding and removing a suffix.
    val dbPath = applicationContext.getDatabasePath("remove")
        .absolutePath.removeSuffix("/remove")

    val result = nativeApi.bootstrapCore(dbPath, "debug", true,
        JniLogCallback())
    if (result.status != 1) {
        error("Couldn't bootstrap core: status: ${result.status}, message: ${result.message}")
    }
}
