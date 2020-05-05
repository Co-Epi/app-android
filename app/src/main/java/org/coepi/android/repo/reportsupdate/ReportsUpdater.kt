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
import org.coepi.android.tcn.TcnDao
import org.coepi.android.tcn.TcnReport
import org.coepi.android.tcn.TcnReportDao
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
import org.coepi.android.extensions.retrofit.executeSafe
import org.coepi.android.extensions.toHex
import org.coepi.android.extensions.toResult
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.LAST_COMPLETED_REPORTS_INTERVAL
import org.coepi.android.system.log.LogTag.TCN_MATCHING
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.VoidOperationState
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
        val reports: List<SignedReport> = newMatchingReports().successOrNull() ?: return
        val insertedCount = storeReports(reports)
        if (insertedCount > 0) {
            newAlertsNotificationShower.showNotification(insertedCount)
        }
    }

    /**
     * Stores reports in the database
     * @return count of inserted reports. This can differ from reports count, if reports
     * are already in the db.
     */
    private fun storeReports(reports: List<SignedReport>): Int {
        val insertedCount: Int = reports.map {
            reportsDao.insert(TcnReport(
                id = it.signature.toByteArray().toHex(),
                report = it.report.memoData.toString(UTF_8),
                timestamp = now().value // TODO extract this from memo, when protocol implemented
            ))
        }.filter { it }.size

        if (insertedCount >= 0) {
            log.d("Added $insertedCount new reports")
        }

        return insertedCount
    }

    private fun newMatchingReports(): Result<List<SignedReport>, Throwable> {
        val now: UnixTime = now()

        val startInterval: ReportsInterval = preferences.getObject(
            LAST_COMPLETED_REPORTS_INTERVAL,
            ReportsInterval::class.java
        )?.next() ?: ReportsInterval.createFor(now)

        return matchingReports(startInterval, now).doIfSuccess { chunks ->
            val lastCompletedInterval = chunks
                .map { it.interval }
                .findLastEndingBefore(now)

            lastCompletedInterval?.let {
                preferences.putObject(LAST_COMPLETED_REPORTS_INTERVAL, it)
            }

        }.map { chunks ->
            chunks.flatMap { it.matched }
        }
    }

    private fun List<ReportsInterval>.findLastEndingBefore(time: UnixTime): ReportsInterval? =
        reversed().find { it.endsBefore(time) }

    private fun matchingReports(startInterval: ReportsInterval, until: UnixTime)
            : Result<List<ProcessedReportsChunk>, Throwable> =
        generateSequence(startInterval) { it.next() }
            .takeWhile { it.startsBefore(until) }
            .map { retrieveMatchingReports(it) }
            .asIterable()
            .map {
                when (it) {
                    is Success -> it.success
                    is Failure -> return@matchingReports Failure(
                        Throwable("Error fetching reports: ${it.error}"))
                }
            }
            .let { Success(it) }

    private fun retrieveMatchingReports(interval: ReportsInterval)
            : Result<ProcessedReportsChunk, Throwable> {

        updateState.onNext(Progress)

        val reportsResult: Result<List<String>, Throwable> = api.getReports(interval.number,
//            interval.length)
            // TODO api will probably change this to seconds
            interval.length * 1000)

            .executeSafe()
            .flatMap { it.toResult() }

        reportsResult.doIfSuccess { reports ->
            log.i("Retrieved ${reports.size} reports. Start matching...", TCN_MATCHING)
        }

        val result = reportsResult.map { reports ->
            val matches = findMatches(reports)
            ProcessedReportsChunk(
                reports,
                matches,
                interval
            )
        }

        return result.also {
            updateState.onNext(when (result) {
                is Success -> OperationState.Success(Unit)
                is Failure -> OperationState.Failure(result.error).also {
                    log.e("Error updating reports: ${result.error}")
                }
            })
            updateState.onNext(NotStarted)
        }
    }

    private fun findMatches(reports: List<String>): List<SignedReport> {
        val matchingStartTime = currentTimeMillis()

        val matchedReports: List<SignedReport> = tcnDao.all().map { it.tcn }.let { tcns ->
            log.i("DB TCNs count: ${tcns.size}")
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
}
