package org.coepi.android.api

import io.reactivex.Completable
import io.reactivex.Single
import org.coepi.android.api.request.ApiParamsCenReport
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CENApi {
    // post CENReport along with CENKeys
    @POST("cenreport/")
    fun postCENReport(@Body report : ApiParamsCenReport): Completable

    // get recent keys that have CEN Reports
    @GET("cenkeys/{timestamp}")
    fun cenkeysCheck(@Path("timestamp") timestamp : Int): Single<List<String>>

    // get report based on matched CENkey
    @GET("cenreport/{key}")
    fun getCenReports(@Path("key") key: String): Single<List<ApiCenReport>>
}
