package org.coepi.android.api

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TcnApi {

    @GET("tcnreport/0.4.0")
    fun getReports(@Query("intervalNumber") intervalNumber: Long,
                   @Query("intervalLength") intervalLength: Long): Call<List<String>>

    @POST("tcnreport/0.4.0")
    fun postReport(@Body report: RequestBody): Call<Unit>
}
