package org.coepi.android.components.noop

import io.reactivex.Completable
import okhttp3.RequestBody
import org.coepi.android.api.TcnApi
import retrofit2.Call

class NoOpTcnApi : TcnApi {
    override fun getReports(intervalNumber: Long, intervalLength: Long): Call<List<String>> =
        NoOpCall()
    override fun postReport(report: RequestBody): Completable =
        Completable.complete()
}
