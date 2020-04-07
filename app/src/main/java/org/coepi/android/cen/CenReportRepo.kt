package org.coepi.android.cen

import io.reactivex.Completable
import io.reactivex.Completable.complete
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.api.CENApi
import org.coepi.android.api.request.ApiParamsCenReport
import org.coepi.android.api.request.toApiParamsCenReport
import org.coepi.android.extensions.toObservable
import org.coepi.android.system.log.LogTag.NET
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.system.rx.VoidOperationState.Progress
import org.coepi.android.system.rx.VoidOperationStateConsumer
import java.util.concurrent.TimeUnit.SECONDS

interface CenReportRepo {
    val sendState: Observable<VoidOperationState>

    fun sendReport(report: SymptomReport)
}

class CenReportRepoImpl(
    private val api: CENApi,
    private val cenKeyDao: RealmCenKeyDao
) : CenReportRepo {
    private val postSymptomsTrigger: PublishSubject<SymptomReport> = create()

    override val sendState: PublishSubject<VoidOperationState> = create()

    private val disposables = CompositeDisposable()

    init {
        disposables += postSymptomsTrigger.doOnNext {
            sendState.onNext(Progress)
        }
        .flatMap { report -> postReport(report).toObservable(Unit).materialize() }
        .subscribe(VoidOperationStateConsumer(sendState))
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
