package org.coepi.android.api

class NativeApi {

    init
    {
        System.loadLibrary("tcn_client")
    }

    external fun getReports(pattern: String): String

    external fun postReport(pattern: String): String

}