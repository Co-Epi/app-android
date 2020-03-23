package org.coepi.android.network.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.POST

interface ExposureApi {
    @GET("")
    fun checkExposure(): Single<Unit>

    @GET("")
    fun confirmExposure(): Single<Unit>

    @POST("")
    fun recordExposureAndSymptoms(): Single<Unit>
}
