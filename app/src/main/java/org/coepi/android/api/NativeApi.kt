package org.coepi.android.api

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

class NativeApi {

    init {
        System.loadLibrary("coepi_core")
    }

    external fun bootstrapCore(db_path: String): String

    external fun clearSymptoms(): String

    external fun fetchNewReports(): String

    external fun generateTcn(): String

    external fun recordTcn(c_tcn: String): String

    external fun setBreathlessnessCause(c_cause: String): String

    external fun setCoughDays(c_is_set: UByte, c_days: UInt): String

    external fun setCoughStatus(c_status: String): String

    external fun setCoughType(c_cough_type: String): String

    external fun setEarliestSymptomStartedDaysAgo(c_is_set:UByte, c_days: UInt): String

    external fun setFeverDays(c_is_set:UByte, c_days: UInt): String

    external fun setFeverHighestTemperatureTaken(c_is_set: UByte, c_temp: Float): String

    external fun setFeverTakenTemperatureSpot(c_cause: String): String

    external fun setFeverTakenTemperatureToday(c_is_set: UByte, c_taken: UByte): String

    external fun setSymptomIds(c_ids: String): String

    external fun submitSymptoms(): String

    external fun triggerLoggingMacros(): Int

    // Tests ////////////////////////////////////////////////////////////////////////

    // Basic

    external fun sendReceiveString(string: String): String

    external fun passStruct(myStruct: FFIParameterStruct): Int

    external fun returnStruct(): FFIParameterStruct

    external fun callCallback(callback: Callback): Int

    external fun registerCallback(callback: Callback): Int

    external fun triggerCallback(string: String): Int

    // Domain

    external fun testBootstrapCore(dbPath: String, level: String, coepiOnly: Boolean): JniVoidResult

    external fun testReturnAnAlert(): JniOneAlertResult

    external fun testReturnMultipleAlerts(): JniAlertsArrayResult

    /////////////////////////////////////////////////////////////////////////////////
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

