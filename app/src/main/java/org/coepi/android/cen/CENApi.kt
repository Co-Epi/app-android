package org.coepi.android.cen

import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CENApi {
    // post CENReport along with CENKeys
    @POST("/cenreport")
    fun postCENReport(@Body report : RealmCenReport): Single<Unit>

    // get recent keys that have CEN Reports
    @GET("/cenkeys/{timestamp}")
    fun cenkeysCheck(@Path("timestamp") timestamp : Int): Call<List<String>>

    // get report based on matched CENkey
    @GET("/cenreport/{key}")
    fun getCENReport(@Path("key") key: String): Call<Array<RealmCenReport>>
}
