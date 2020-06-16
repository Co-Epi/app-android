package org.coepi.android.api

data class FFIParameterStruct(
    val myInt: Int,
    val myStr: String,
    val myNested: FFINestedParameterStruct
)

data class FFINestedParameterStruct(
    val myU8: Int
)

// TODO specific for logging
open class Callback {
    open fun log(foo: String) {
        println("callback called with: $foo")
    }
}

open class MyCallback {
    open fun call(par: Int) {
        println("MyCallback called with: $par")
    }
}

class NativeApi {

    init {
        System.loadLibrary("coepi_core")
    }

    // TODO remove
    external fun postReport(c_report: String, callback: Callback): String

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

    external fun sendReceiveString(string: String): String

    external fun passStruct(my_struct: FFIParameterStruct, callback: Callback): Int

    external fun returnStruct(callback: Callback): FFIParameterStruct

    external fun callCallback(callback: Callback): Int

    external fun registerCallback(callback: MyCallback): Int

    external fun triggerCallback(par: Int): Int

    /////////////////////////////////////////////////////////////////////////////////
}
