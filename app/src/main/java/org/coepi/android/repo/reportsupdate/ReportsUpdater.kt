package org.coepi.android.repo.reportsupdate

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.api.TcnApi
import org.coepi.android.common.Result
import org.coepi.android.common.Result.Failure
import org.coepi.android.common.Result.Success
import org.coepi.android.common.doIfSuccess
import org.coepi.android.common.flatMap
import org.coepi.android.common.map
import org.coepi.android.common.successOrNull
import org.coepi.android.domain.TcnMatcher
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.extensions.base64ToByteArray
import org.coepi.android.extensions.retrofit.executeSafe
import org.coepi.android.extensions.toHex
import org.coepi.android.extensions.toResult
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.LAST_COMPLETED_REPORTS_INTERVAL
import org.coepi.android.system.log.LogTag.NET
import org.coepi.android.system.log.LogTag.TCN_MATCHING
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.tcn.TcnDao
import org.coepi.android.tcn.TcnReport
import org.coepi.android.tcn.TcnReportDao
import org.tcncoalition.tcnclient.crypto.SignedReport
import java.lang.System.currentTimeMillis
import java.nio.charset.StandardCharsets.UTF_8

interface ReportsUpdater {
    fun requestUpdateReports()

    val updateState: Observable<VoidOperationState>
}

class ReportsUpdaterImpl(
    private val tcnMatcher: TcnMatcher,
    private val api: TcnApi,
    private val tcnDao: TcnDao,
    private val reportsDao: TcnReportDao,
    private val preferences: Preferences,
    private val newAlertsNotificationShower: NewAlertsNotificationShower
) : ReportsUpdater {

    private val disposables = CompositeDisposable()

    override val updateState: BehaviorSubject<VoidOperationState> = createDefault(NotStarted)

    private val reportsUpdateTrigger: PublishSubject<Unit> = create()

    init {
        disposables += reportsUpdateTrigger
            .observeOn(io())
            .withLatestFrom(updateState)
            .filter { (_, state) -> state !is Progress }
            .subscribe {
                updateReports()
            }
    }

    override fun requestUpdateReports() {
        reportsUpdateTrigger.onNext(Unit)
    }

    private fun updateReports() {
        val reports: List<SignedReport> = retrieveAndMatchNewReports().successOrNull() ?: return
        val insertedCount = storeReports(reports)
        if (insertedCount > 0) {
            newAlertsNotificationShower.showNotification(insertedCount)
        }
    }

    fun retrieveAndMatchNewReports(): Result<List<SignedReport>, Throwable> {
        val now: UnixTime = now()
        return matchingReports(
            startInterval = determineStartInterval(now),
            until = now
        ).doIfSuccess { chunks ->
            storeLastCompletedInterval(chunks.map { it.interval }, now)
        }.map { chunks ->
            chunks.flatMap { it.matched }
        }
    }

    fun determineStartInterval(time: UnixTime): ReportsInterval =
        retrieveLastCompletedInterval()?.next() ?: ReportsInterval.createFor(time)

    fun intervalEndingBefore(intervals: List<ReportsInterval>, time: UnixTime)
            : ReportsInterval? =
        intervals.reversed().find { it.endsBefore(time) }

    /**
     * Retrieves paginated reports from api and matches them.
     * If fetching/processing one chunk fails, it makes the whole operation fail.
     */
    fun matchingReports(startInterval: ReportsInterval, until: UnixTime)
            : Result<List<MatchedReportsChunk>, Throwable> =
        generateIntervalsSequence(startInterval, until)
            .also { updateState.onNext(Progress) }
            .map { retrieveReports(it) }
            .map { matchRetrievedReportsResult(it) }
            .asIterable()
            .map {
                when (it) {
                    is Success -> it.success
                    is Failure -> return@matchingReports Failure(
                        Throwable("Error fetching reports: ${it.error}"))
                }
            }
            .let { Success(it) }

    /**
     * Returns (lazy) interval sequence starting with from, until the last interval of which the
     * start is before until.
     * This implies that if an interval starts exactly at until, it will not be included.
     */
    fun generateIntervalsSequence(from: ReportsInterval, until: UnixTime): Sequence<ReportsInterval> =
        generateSequence(from) { it.next() }
            .takeWhile { it.startsBefore(until) }

    fun retrieveReports(interval: ReportsInterval): Result<SignedReportsChunk, Throwable> {
        val reportsStringsResult: Result<List<String>, Throwable> = api.getReports(interval.number,
            interval.length)
            .executeSafe()
            .flatMap { it.toResult() }

        reportsStringsResult.doIfSuccess { reports ->
            log.i("Retrieved ${reports.size} reports.", NET)
        }

        return reportsStringsResult.map { reportStrings ->
            SignedReportsChunk(
                interval,
                reportStrings.mapNotNull { reportString ->
                    toSignedReport(reportString).also {
                        if (it == null) {
                            log.e("Failed to convert report string: $it to report")
                        }
                    }
                }
            )
        }
    }

    /**
     * Matches the reports and updates operation state observable.
     */
    fun matchRetrievedReportsResult(reportsResult: Result<SignedReportsChunk, Throwable>)
            : Result<MatchedReportsChunk, Throwable> =
        reportsResult.map { chunk ->
            toMatchedReportsChunk(chunk)
        }.also {
            updateOperationStateWithMatchResult(it)
        }

    /**
     * Maps reports chunk to a new chunk containing possible matches.
     */
    fun toMatchedReportsChunk(chunk: SignedReportsChunk): MatchedReportsChunk =
        MatchedReportsChunk(chunk.reports, findMatches(chunk.reports), chunk.interval)

    fun findMatches(reports: List<SignedReport>): List<SignedReport> {
        val matchingStartTime = currentTimeMillis()
        log.i("Start matching...", TCN_MATCHING)

        val matchedReports: List<SignedReport> = tcnDao.all().map { it.tcn }.let { tcns ->
            log.i("DB TCNs count: ${tcns.size}")
            // TODO review: Do we still need distinct? Does the api still send repeated reports?
            tcnMatcher.match(tcns, reports.distinct())
        }

        val time = (currentTimeMillis() - matchingStartTime) / 1000
        log.i("Took ${time}s to match reports", TCN_MATCHING)

        if (matchedReports.isNotEmpty()) {
            log.i("Matches found (${matchedReports.size}): $matchedReports", TCN_MATCHING)
        } else {
            log.i("No matches found", TCN_MATCHING)
        }

        return matchedReports
    }

    /**
     * Stores reports in the database
     * @return count of inserted reports. This can differ from reports count, if reports
     * are already in the db.
     */
    fun storeReports(reports: List<SignedReport>): Int {
        val insertedCount: Int = reports.map {
            reportsDao.insert(TcnReport(
                id = it.signature.toByteArray().toHex(),
                memoStr = it.report.memoData.toString(UTF_8),
                timestamp = now().value // TODO extract this from memo, when protocol implemented
            ))
        }.filter { it }.size

        if (insertedCount >= 0) {
            log.d("Added $insertedCount new reports")
        }

        return insertedCount
    }

    private fun updateOperationStateWithMatchResult(result: Result<MatchedReportsChunk, Throwable>) {
        updateState.onNext(when (result) {
            is Success -> OperationState.Success(Unit)
            is Failure -> OperationState.Failure(result.error).also {
                log.e("Error updating reports: ${result.error}")
            }
        })
        updateState.onNext(NotStarted)
    }

    private fun toSignedReport(reportString: String): SignedReport? =
        reportString.base64ToByteArray()?.let { SignedReport.fromByteArray(it) }

    private fun retrieveLastCompletedInterval(): ReportsInterval? =
        preferences.getObject(LAST_COMPLETED_REPORTS_INTERVAL, ReportsInterval::class.java)

    private fun storeLastCompletedInterval(intervals: List<ReportsInterval>, now: UnixTime) {
        intervalEndingBefore(intervals, now)?.let {
            // TODO (optional) closer inspection of relationship of now to intervals, for better logging/testing
            preferences.putObject(LAST_COMPLETED_REPORTS_INTERVAL, it)
        }
    }
}
