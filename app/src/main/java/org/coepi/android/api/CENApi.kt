package org.coepi.android.api

import io.reactivex.Completable
import org.coepi.android.api.request.ApiParamsCenReport
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CENApi {

    @GET("tcnreport/")
    fun getReports(): Call<List<String>>

    @POST("tcnreport/")
    fun postReport(@Body report : String): Completable

    // post CENReport along with CENKeys
    @POST("cenreport/")
    fun postCENReport(@Body report : ApiParamsCenReport): Completable

    // get recent keys that have CEN Reports
    @GET("cenkeys/")
    fun cenkeysCheck(): Call<List<String>>

    // get report based on matched CENkey
    @GET("cenreport/{key}")
    fun getCenReports(@Path("key") key: String): Call<List<ApiCenReport>>
}
