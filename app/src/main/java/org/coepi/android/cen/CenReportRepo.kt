package org.coepi.android.cen

import io.reactivex.Completable
import io.reactivex.Completable.complete
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.api.CENApi
import org.coepi.android.api.request.ApiParamsCenReport
import org.coepi.android.api.request.toApiParamsCenReport
import org.coepi.android.extensions.toObservable
import org.coepi.android.system.log.LogTag.NET
import org.coepi.android.system.log.log

interface CenReportRepo {
    fun sendReport(report: SymptomReport)
}

class CenReportRepoImpl(
    private val api: CENApi,
    private val cenKeyDao: RealmCenKeyDao
) : CenReportRepo {
    private val postSymptomsTrigger: PublishSubject<SymptomReport> = create()

    private val disposables = CompositeDisposable()

    init {
        disposables += postSymptomsTrigger.flatMap { report ->
            postReport(report).toObservable(Unit)
        }.subscribeBy(onComplete = {
            log.i("Posted symptoms to the api", NET)
        }, onError = {
            log.i("Error posting symptoms to api: $it", NET)
        })
    }

    override fun sendReport(report: SymptomReport) {
        postSymptomsTrigger.onNext(report)
    }

    private fun postReport(report: SymptomReport): Completable {
        val params: ApiParamsCenReport? =
            cenKeyDao.lastCENKeys(3).takeIf { it.isNotEmpty() }?.let { keys ->
                report.toApiParamsCenReport(keys.map { it.toCenKey() })
            }
        return if (params != null) {
            log.i("Posting the CEN report to the Api")
            api.postCENReport(params).subscribeOn(io())
        } else {
            log.e("Can't post report. No CEN keys.", NET)
            complete()
        }
    }
}
