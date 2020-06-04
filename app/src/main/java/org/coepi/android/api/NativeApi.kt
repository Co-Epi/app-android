package org.coepi.android.api

class NativeApi {

    init
    {
        System.loadLibrary("coepi_core")
    }

    external fun getReports(pattern: String): String

    external fun postReport(pattern: String): String

}