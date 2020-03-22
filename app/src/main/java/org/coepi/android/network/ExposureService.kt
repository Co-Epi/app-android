package org.coepi.android.network

import retrofit2.http.GET
import retrofit2.http.POST

interface ExposureService {
    @GET("")
    fun checkExposure() {

    }

    @GET("")
    fun confirmExposure() {
        
    }

    @POST("")
    fun recordExposureAndSymptoms() {

    }
}
