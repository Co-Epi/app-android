package org.coepi.android.repo

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import org.coepi.android.api.CENApi
import org.coepi.android.api.toCenReport
import org.coepi.android.cen.CenReportRepo
import org.coepi.android.cen.RealmCenDao
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.cen.SymptomReport
import org.coepi.android.cross.CenKeysFetcher
import org.coepi.android.domain.CenMatcher
import org.coepi.android.extensions.coEpiTimestamp
import org.coepi.android.extensions.rx.doOnNextSuccess
import org.coepi.android.extensions.rx.flatMapSuccess
import org.coepi.android.extensions.rx.mapSuccess
import org.coepi.android.extensions.rx.toOperationState
import org.coepi.android.system.log.LogTag.CEN_L
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationForwarder
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.Progress
import java.lang.System.currentTimeMillis
import java.util.Date

interface CoEpiRepo {
    // Infection reports fetched periodically from the API
    val reports: BehaviorSubject<OperationState<List<ReceivedCenReport>>>

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
    private val reportRepo: CenReportRepo
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

    init {
        disposables += reportsObservable.subscribe(OperationForwarder(reports))
    }

    override fun sendReport(report: SymptomReport) {
        reportRepo.sendReport(report)
    }

    override fun storeObservedCen(cen: ReceivedCen) {
        log.v("Storing an observed CEN: $cen")
        cenDao.insert(cen)
    }
}
