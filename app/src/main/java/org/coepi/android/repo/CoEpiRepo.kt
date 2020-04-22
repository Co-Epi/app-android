package org.coepi.android.repo

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
import org.coepi.android.api.toCenReport
import org.coepi.android.cen.Cen
import org.coepi.android.cen.CenKey
import org.coepi.android.cen.CenDao
import org.coepi.android.cen.CenKeyDao
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.cen.SymptomReport
import org.coepi.android.common.ApiSymptomsMapper
import org.coepi.android.common.Result
import org.coepi.android.common.Result.Failure
import org.coepi.android.common.Result.Success
import org.coepi.android.common.doIfSuccess
import org.coepi.android.common.flatMap
import org.coepi.android.common.group
import org.coepi.android.common.map
import org.coepi.android.domain.CenMatcher
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.extensions.rx.toObservable
import org.coepi.android.extensions.toResult
import org.coepi.android.system.log.LogTag.CEN_MATCHING
import org.coepi.android.system.log.LogTag.NET
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationStateNotifier
import org.coepi.android.system.rx.VoidOperationState
import java.lang.System.currentTimeMillis

interface CoEpiRepo {

    // State of send report operation
    val sendReportState: Observable<VoidOperationState>

    // Store CEN from other device
    fun storeObservedCen(cen: ReceivedCen)

    // Send symptoms report
    fun sendReport(report: SymptomReport)

    fun reports(): Result<List<ReceivedCenReport>, Throwable>
}

class CoepiRepoImpl(
    private val cenMatcher: CenMatcher,
    private val api: CENApi,
    private val cenDao: CenDao,
    private val cenKeyDao: CenKeyDao,
    private val symptomsProcessor: ApiSymptomsMapper
) : CoEpiRepo {

    private var matchingStartTime: Long? = null

    private val disposables = CompositeDisposable()

    private val postSymptomsTrigger: PublishSubject<SymptomReport> = create()
    override val sendReportState: PublishSubject<VoidOperationState> = create()

    init {
        disposables += postSymptomsTrigger.doOnNext {
            sendReportState.onNext(Progress)
        }
        .flatMap { report -> postReport(report).toObservable(Unit).materialize() }
        .subscribe(OperationStateNotifier(sendReportState))
    }

    override fun sendReport(report: SymptomReport) {
        postSymptomsTrigger.onNext(report)
    }

    override fun reports(): Result<List<ReceivedCenReport>, Throwable> {
        val keysResult = api.cenkeysCheck().execute()
            .toResult().map { keyStrings ->
                keyStrings.map {
                    CenKey(it, now())
                }
            }

        keysResult.doIfSuccess { keys ->
            log.i("Retrieved ${keys.size} keys. Start matching...", CEN_MATCHING)
            val keyStrings = keys.map { it.key }
            log.v("$keyStrings", CEN_MATCHING)
        }

        matchingStartTime = currentTimeMillis()

        val matchedKeysResult: Result<List<CenKey>, Throwable> =
            keysResult.map { filterMatchingKeys(it) }

        matchingStartTime?.let {
            val time = (currentTimeMillis() - it) / 1000
            log.i("Took ${time}s to match keys", CEN_MATCHING)
        }
        matchingStartTime = null

        matchedKeysResult.doIfSuccess {
            if (it.isNotEmpty()) {
                log.i("Matches found: $it", CEN_MATCHING)
            } else {
                log.i("No matches found", CEN_MATCHING)
            }
        }

        return matchedKeysResult.flatMap { reportsFor(it) }
    }

    private fun reportsFor(keys: List<CenKey>): Result<List<ReceivedCenReport>, Throwable> {

        // Retrieve reports for keys, group in successful / failed calls
        val (successful, failed) = keys.map { key ->
            api.getCenReports(key.key).execute().toResult()
        }.group()

        // Log failed calls
        failed.forEach {
            log.e("Error fetching reports: $it")
        }

        // If we only got failure results, return a failure, otherwise return success
        // and ignore failures (logged before)
        // TODO review / maybe refine this error handling
        return if (successful.isEmpty() && failed.isNotEmpty()) {
            Failure(Throwable("Couldn't fetch any reports"))
        } else {
            Success(successful.flatten().map { ReceivedCenReport(it.toCenReport()) })
        }
    }

    private fun filterMatchingKeys(keys: List<CenKey>): List<CenKey> {
        val maxDate: UnixTime = now()
        // TODO delete periodically entries older than ~3 weeks from the db
        val cens: List<Cen> = cenDao.all().map { it.cen }
        log.i("Stored CENs: ${cens.size}")
        return cenMatcher.match(cens, keys.distinct(), maxDate)
    }

    private fun postReport(report: SymptomReport): Completable {
        val params: ApiParamsCenReport? =
            cenKeyDao.lastCENKeys(3).takeIf { it.isNotEmpty() }?.let { keys ->
                symptomsProcessor.toApiReport(report, keys)
            }
        return if (params != null) {
            log.i("Sending CEN report to API: $params", NET)
            api.postCENReport(params).subscribeOn(io())
        } else {
            log.e("Can't send report. No CEN keys.", NET)
            complete()
        }
    }

    override fun storeObservedCen(cen: ReceivedCen) {
        if (cenDao.insert(cen)) {
            log.v("Inserted an observed CEN: $cen")
        }
    }
}
