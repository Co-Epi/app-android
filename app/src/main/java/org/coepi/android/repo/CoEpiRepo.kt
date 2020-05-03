package org.coepi.android.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.coepi.android.R.drawable.ic_launcher_foreground
import org.coepi.android.R.plurals
import org.coepi.android.R.string
import org.coepi.android.api.CENApi
import org.coepi.android.cen.CenDao
import org.coepi.android.cen.CenReport
import org.coepi.android.cen.CenReportDao
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.cen.SymptomReport
import org.coepi.android.common.ApiSymptomsMapper
import org.coepi.android.common.Result
import org.coepi.android.common.Result.Failure
import org.coepi.android.common.Result.Success
import org.coepi.android.common.doIfSuccess
import org.coepi.android.common.flatMap
import org.coepi.android.common.map
import org.coepi.android.common.successOrNull
import org.coepi.android.domain.CenMatcher
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.extensions.retrofit.executeSafe
import org.coepi.android.extensions.rx.toObservable
import org.coepi.android.extensions.toHex
import org.coepi.android.extensions.toResult
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.LAST_COMPLETED_REPORTS_INTERVAL
import org.coepi.android.system.Resources
import org.coepi.android.system.intent.IntentKey.NOTIFICATION_INFECTION_ARGS
import org.coepi.android.system.intent.IntentNoValue
import org.coepi.android.system.log.LogTag.CEN_MATCHING
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationStateNotifier
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.ui.notifications.AppNotificationChannels
import org.coepi.android.ui.notifications.NotificationConfig
import org.coepi.android.ui.notifications.NotificationIntentArgs
import org.coepi.android.ui.notifications.NotificationPriority.HIGH
import org.coepi.android.ui.notifications.NotificationsShower
import org.tcncoalition.tcnclient.crypto.SignedReport
import java.lang.System.currentTimeMillis
import java.nio.charset.StandardCharsets.UTF_8

// TODO remove CoEpiRepo. Create update reports use case and send TCNs directly to DAO
// TODO if Rust library or similar is added later, we can re-add this and inject new use case in it
interface CoEpiRepo {

    // State of send report operation
    val sendReportState: Observable<VoidOperationState>

    // Store CEN from other device
    fun storeObservedCen(cen: ReceivedCen)

    // Send symptoms report
    fun sendReport(report: SymptomReport)

    // Trigger manually report update
    fun requestUpdateReports()

    val updateReportsState: Observable<VoidOperationState>
}

class CoepiRepoImpl(
    private val cenMatcher: CenMatcher,
    private val api: CENApi,
    private val cenDao: CenDao,
    private val symptomsProcessor: ApiSymptomsMapper,
    private val reportsDao: CenReportDao,
    private val resources: Resources,
    private val notificationChannelsInitializer: AppNotificationChannels,
    private val notificationsShower: NotificationsShower,
    private val preferences: Preferences
) : CoEpiRepo {

    private var matchingStartTime: Long? = null

    private val disposables = CompositeDisposable()

    private val postSymptomsTrigger: PublishSubject<SymptomReport> = create()
    override val sendReportState: PublishSubject<VoidOperationState> = create()

    override val updateReportsState: BehaviorSubject<VoidOperationState> = createDefault(NotStarted)

    private val reportsUpdateTrigger: PublishSubject<Unit> = create()

    init {
        disposables += postSymptomsTrigger.doOnNext {
            sendReportState.onNext(Progress)
        }
        .flatMap { report -> postReport(report)
            .doOnError { log.e("Error posting report: ${it.message}") }
            .toObservable(Unit)
            .materialize()
        }
        .subscribe(OperationStateNotifier(sendReportState))

        disposables += reportsUpdateTrigger
            .observeOn(io())
            .withLatestFrom(updateReportsState)
            .filter { (_, state) -> state !is Progress }
            .subscribe {
                updateReports()
            }
    }

    override fun requestUpdateReports() {
        reportsUpdateTrigger.onNext(Unit)
    }

    override fun sendReport(report: SymptomReport) {
        postSymptomsTrigger.onNext(report)
    }

    private fun updateReports() {
        val reportsResult: Result<List<SignedReport>, Throwable> = newMatchingReports()
        val reports: List<SignedReport> = reportsResult.successOrNull() ?: emptyList()

        val insertedCount: Int = reports.map {
            reportsDao.insert(CenReport(
                id = it.signature.toByteArray().toHex(),
                report = it.report.memoData.toString(UTF_8),
                timestamp = now().value // TODO extract this from memo, when protocol implemented
            ))
        }.filter { it }.size

        if (insertedCount >= 0) {
            log.d("Added $insertedCount new reports")
        }

        if (insertedCount > 0) {
            log.d("Showing notification...")
            notificationsShower.showNotification(notificationConfiguration(insertedCount))
        }
    }

    private fun notificationConfiguration(count: Int): NotificationConfig = NotificationConfig(
        ic_launcher_foreground,
        resources.getString(string.infection_notification_title),
        resources.getQuantityString(plurals.alerts_new_notifications_count, count),
        HIGH,
        notificationChannelsInitializer.reportsChannelId,
        NotificationIntentArgs(NOTIFICATION_INFECTION_ARGS, IntentNoValue())
    )

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

    data class ProcessedReportsChunk(val reports: List<String>, val matched: List<SignedReport>,
                                     val interval: ReportsInterval)

    data class ReportsInterval(val number: Long, val length: Long) {
        fun next(): ReportsInterval = ReportsInterval(number + 1, length)
        val start: Long = number * length
        val end: Long = start + length

        fun startsBefore(time: UnixTime): Boolean = start < time.value

        fun endsBefore(time: UnixTime): Boolean = end < time.value

        companion object {
            fun createFor(time: UnixTime): ReportsInterval {
                val intervalLengthSeconds = 21600L
                return ReportsInterval(
                    number = time.value / intervalLengthSeconds,
                    length  = intervalLengthSeconds
                )
            }
        }
    }

    private fun retrieveMatchingReports(interval: ReportsInterval)
            : Result<ProcessedReportsChunk, Throwable> {

        updateReportsState.onNext(Progress)

        val reportsResult: Result<List<String>, Throwable> = api.getReports(interval.number,
//            interval.length)
            // TODO api will probably change this to seconds
            interval.length * 1000)

            .executeSafe()
            .flatMap { it.toResult() }

        reportsResult.doIfSuccess { reports ->
            log.i("Retrieved ${reports.size} reports. Start matching...", CEN_MATCHING)
        }

        val result = reportsResult.map { reports ->
            val matches = findMatches(reports)
            ProcessedReportsChunk(reports, matches, interval)
        }

        return result.also {
            updateReportsState.onNext(when (result) {
                is Success -> OperationState.Success(Unit)
                is Failure -> OperationState.Failure(result.error).also {
                    log.e("Error updating reports: ${result.error}")
                }
            })
            updateReportsState.onNext(NotStarted)
        }
    }

    private fun findMatches(reports: List<String>): List<SignedReport> {

        matchingStartTime = currentTimeMillis()

        log.i("Retrieved ${reports.size} reports. Start matching...", CEN_MATCHING)

        val matchedReports: List<SignedReport> = cenDao.all().map { it.cen }.let { cens ->
            log.i("DB CENs count: ${cens.size}")
            cenMatcher.match(cens, reports.distinct())
        }

        matchingStartTime?.let {
            val time = (currentTimeMillis() - it) / 1000
            log.i("Took ${time}s to match reports", CEN_MATCHING)
        }
        matchingStartTime = null

        if (matchedReports.isNotEmpty()) {
            log.i("Matches found (${matchedReports.size}): $matchedReports", CEN_MATCHING)
        } else {
            log.i("No matches found", CEN_MATCHING)
        }

        return matchedReports
    }

    private fun postReport(report: SymptomReport): Completable =
        symptomsProcessor.toApiReport(report).let { apiReport ->
            // NOTE: Needs to be sent as text/plain to not add quotes
            val requestBody = apiReport.toRequestBody("text/plain".toMediaType())
            api.postReport(requestBody).subscribeOn(io())
        }

    override fun storeObservedCen(cen: ReceivedCen) {
        if (cenDao.insert(cen)) {
            log.v("Inserted an observed CEN: $cen")
        }
    }
}
