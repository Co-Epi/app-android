package org.coepi.android.cen

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.coepi.android.api.CENApi
import org.coepi.android.common.ApiSymptomsMapper
import org.coepi.android.extensions.rx.toObservable
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationStateNotifier
import org.coepi.android.system.rx.VoidOperationState

interface CenReportRepo {
    val reports: Observable<List<SymptomReport>>

    val sendState: Observable<VoidOperationState>

    fun send(report: SymptomReport)

    fun delete(report: SymptomReport)
}

class CenReportRepoImpl(
    private val cenReportDao: CenReportDao,
    private val symptomsProcessor: ApiSymptomsMapper,
    private val api: CENApi
) : CenReportRepo {
    private val disposables = CompositeDisposable()

    private val postSymptomsTrigger: PublishSubject<SymptomReport> = PublishSubject.create()

    override val reports: Observable<List<SymptomReport>> = cenReportDao.reports.map { reports ->
        reports.map {
            symptomsProcessor.fromCenReport(it.report)
        }
    }

    override val sendState: PublishSubject<VoidOperationState> = PublishSubject.create()

    init {
        disposables += postSymptomsTrigger
            .doOnNext { sendState.onNext(Progress) }
            .flatMap { report -> post(report)
                .doOnError { log.e("Error posting report: ${it.message}") }
                .toObservable(Unit)
                .materialize()
            }
            .subscribe(OperationStateNotifier(sendState))
    }

    override fun send(report: SymptomReport) {
        postSymptomsTrigger.onNext(report)
    }

    private fun post(report: SymptomReport): Completable =
        symptomsProcessor.toApiReport(report).let { apiReport ->
            // NOTE: Needs to be sent as text/plain to not add quotes
            val requestBody = apiReport.toRequestBody("text/plain".toMediaType())
            api.postReport(requestBody).subscribeOn(Schedulers.io())
        }

    override fun delete(report: SymptomReport) {
        cenReportDao.delete(report)
    }
}
