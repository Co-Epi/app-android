package org.coepi.android.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import io.reactivex.subjects.PublishSubject
import org.coepi.android.api.CENApi
import org.coepi.android.api.request.ApiParamsCenReport
import org.coepi.android.api.request.toApiParamsCenReport
import org.coepi.android.api.toCenReport
import org.coepi.android.cen.CenKey
import org.coepi.android.cen.CenReport
import org.coepi.android.cen.RealmCenDao
import org.coepi.android.cen.RealmCenKeyDao
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.cen.SymptomReport
import org.coepi.android.cen.toCenKey
import org.coepi.android.cross.CenKeysFetcher
import org.coepi.android.domain.CenMatcher
import org.coepi.android.extensions.coEpiTimestamp
import org.coepi.android.extensions.rx.doOnNextSuccess
import org.coepi.android.extensions.rx.flatMapSuccess
import org.coepi.android.extensions.rx.mapSuccess
import org.coepi.android.extensions.rx.toObservable
import org.coepi.android.extensions.rx.toOperationState
import org.coepi.android.system.log.LogTag.CEN_L
import org.coepi.android.system.log.LogTag.NET
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationForwarder
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationStateNotifier
import org.coepi.android.system.rx.VoidOperationState
import java.lang.System.currentTimeMillis
import java.util.Date

interface CoEpiRepo {
    // Infection reports fetched periodically from the API
    val reports: BehaviorSubject<OperationState<List<ReceivedCenReport>>>

    // State of send report operation
    val sendReportState: Observable<VoidOperationState>

    // Store CEN from other device
    fun storeObservedCen(cen: ReceivedCen)

    // Send symptoms report
    fun sendReport(report: SymptomReport)
}

class CoepiRepoImpl(
    keysFetcher: CenKeysFetcher,
    private val cenMatcher: CenMatcher,
    private val api: CENApi,
    private val cenDao: RealmCenDao,
    private val cenKeyDao: RealmCenKeyDao
) : CoEpiRepo {

    private var matchingStartTime: Long? = null

    // last time (unix timestamp) the CENKeys were requested. TODO use it. From preferences.
    private var lastCENKeysCheck: Long = 0L

    private val disposables = CompositeDisposable()

    override val reports: BehaviorSubject<OperationState<List<ReceivedCenReport>>> =
        createDefault(Progress)

    private val reportsObservable: Observable<OperationState<List<ReceivedCenReport>>> =
        keysFetcher.keys
            .subscribeOn(io())
            .doOnSubscribe {
                reports.onNext(Progress)
            }
            .doOnNextSuccess { keys ->
                matchingStartTime = currentTimeMillis()
                log.i("Fetched keys from API (${keys.size}), start matching...")
            }

//             // Uncomment this to benchmark a few keys quickly...
//            .mapSuccess { keys ->
//                keys.subList(0, 2)
//            }

            // Filter matching keys
            .mapSuccess { keys ->
                keys.distinct().mapNotNull { key ->
                    //.distinct():same key may arrive more than once, due to multiple reporting
                    if (cenMatcher.hasMatches(key, Date().coEpiTimestamp())) {
                        key
                    } else {
                        null
                    }
                }
            }

            .doOnNextSuccess { matchedKeys ->
                matchingStartTime?.let {
                    val time = (currentTimeMillis() - it) / 1000
                    log.i("Took ${time}s to match keys", CEN_L)
                }
                if (matchedKeys.isNotEmpty()) {
                    log.i("Matches found for keys: $matchedKeys")
                } else {
                    log.i("No matches found for keys")
                }
            }

            .flatMapSuccess { matchedKeys ->
                val requests: List<Observable<List<ReceivedCenReport>>> = matchedKeys.map { key ->
                    api.getCenReports(key.key)
                        .map { apiCenReports ->
                            apiCenReports.map { ReceivedCenReport(it.toCenReport()) }
                        }
                        .toObservable()
                }
                Observable.merge(requests).materialize().toOperationState()
            }
            .share()

    private val postSymptomsTrigger: PublishSubject<SymptomReport> = PublishSubject.create()
    override val sendReportState: PublishSubject<VoidOperationState> = PublishSubject.create()

    init {
        disposables += reportsObservable.subscribe(OperationForwarder(reports))

        disposables += postSymptomsTrigger.doOnNext {
            sendReportState.onNext(Progress)
        }
        .flatMap { report -> postReport(report).toObservable(Unit).materialize() }
        .subscribe(OperationStateNotifier(sendReportState))
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
            Completable.complete()
        }
    }

    override fun storeObservedCen(cen: ReceivedCen) {
        log.v("Storing an observed CEN: $cen")
        cenDao.insert(cen)
    }
}
