package org.coepi.android.cen

import io.reactivex.Single
import org.coepi.android.cen.CENKeys
import org.coepi.android.cen.CENReport
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CENApi {
    // post CENReport along with CENKeys
    @POST("/cenreport")
    fun postCENReport(@Body report : CENReport): Single<Unit>

    // get recent keys that have CEN Reports
    @GET("/cenkeys/{timestamp}")
    fun cenkeysCheck(@Path("timestamp") timestamp : Int): Call<CENKeys>

    // get report based on matched CENkey
    @GET("/cenreport/{key}")
    fun getCENReport(@Path("key") key: String): Call<Array<CENReport>>
}
