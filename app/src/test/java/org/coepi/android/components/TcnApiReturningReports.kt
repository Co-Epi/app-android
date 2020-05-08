package org.coepi.android.components

import io.reactivex.Completable
import okhttp3.RequestBody
import org.coepi.android.api.TcnApi
import org.coepi.android.components.CallReturning
import retrofit2.Call

class TcnApiReturningReports(private val reports: List<String>) :
    TcnApi {
    override fun getReports(intervalNumber: Long, intervalLength: Long): Call<List<String>> =
        CallReturning(reports)

    override fun postReport(report: RequestBody): Completable =
        Completable.complete()
}
