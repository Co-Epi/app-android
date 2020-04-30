package org.coepi.android.api

import io.reactivex.Completable
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CENApi {

    @GET("tcnreport/")
    fun getReports(): Call<List<String>>

    @POST("tcnreport/")
    fun postReport(@Body report: RequestBody): Completable
}
