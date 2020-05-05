package org.coepi.android.api

import io.reactivex.Completable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TcnApi {

    @GET("tcnreport/")
    fun getReports(@Query("intervalNumber") intervalNumber: Long,
                   @Query("intervalLength") intervalLength: Long): Call<List<String>>

    @POST("tcnreport/")
    fun postReport(@Body report: RequestBody): Completable
}
