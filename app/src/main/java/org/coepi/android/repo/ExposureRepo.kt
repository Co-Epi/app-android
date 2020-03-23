package org.coepi.android.repo

import io.reactivex.Single
import org.coepi.android.network.api.ExposureApi

class ExposureRepo(private val exposureApi: ExposureApi) {

    fun checkExposure(): Single<Unit> = exposureApi.checkExposure()

    fun confirmExposure(): Single<Unit> = exposureApi.confirmExposure()

    fun recordExposureAndSymptoms(): Single<Unit> = exposureApi.recordExposureAndSymptoms()
}
