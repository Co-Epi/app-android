package org.coepi.android.cen

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CENApi {
    // post CENReport along with CENKeys
    @POST("/cenreport/") //not needed 13298327ebcebe7f153b956e4596d503"
    fun postCENReport(@Body report : SymptomReport): Call<Unit>

    // get recent keys that have CEN Reports
    @GET("/cenkeys/{timestamp}")
    fun cenkeysCheck(@Path("timestamp") timestamp : Int): Call<List<String>>

    // get report based on matched CENkey
    @GET("/cenreport/{key}")
    fun getCenReport(@Path("key") key: String): Call<List<CenReport>>
}
