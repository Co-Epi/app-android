package org.coepi.android.worker.cenfetcher

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import org.coepi.android.cen.RealmCenReportDao
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.common.doIfSuccess
import org.coepi.android.common.successOrNull
import org.coepi.android.domain.CoEpiDate
import org.coepi.android.domain.CoEpiDate.Companion.fromUnixTime
import org.coepi.android.domain.CoEpiDate.Companion.minDate
import org.coepi.android.domain.CoEpiDate.Companion.now
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.LAST_CEN_KEYS_FETCH_TIMESTAMP
import org.coepi.android.system.log.LogTag.CEN_MATCHING
import org.coepi.android.system.log.log
import org.koin.core.KoinComponent
import org.koin.core.inject

class ContactsFetchWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val coEpiRepo: CoEpiRepo by inject()
    private val reportsDao: RealmCenReportDao by inject()
    private val preferences: Preferences by inject()

    override suspend fun doWork(): Result {
        log.d("Contacts fetch worker started...", CEN_MATCHING)

        val fromDate: CoEpiDate = preferences.getLong(LAST_CEN_KEYS_FETCH_TIMESTAMP)?.let {
            fromUnixTime(it)
        } ?: minDate()

        val nowBeforeRequest: CoEpiDate = now()

        val reportsResult =
            coEpiRepo.reports(fromDate)
        val reports: List<ReceivedCenReport> = reportsResult.successOrNull() ?: emptyList()

        reports.forEach {
            reportsDao.insert(it.report)
        }

        // After reports saved successfully, store the fetch timestamp
        reportsResult.doIfSuccess {
            preferences.putLong(LAST_CEN_KEYS_FETCH_TIMESTAMP, nowBeforeRequest.unixTime)
        }

        setProgress(workDataOf(CONTACT_COUNT_KEY to reports.size))
        // TODO why is this need?
        // TODO without delay, live data never receives the progress
        delay(100)

        log.i("Contacts fetch worker finished. Saved reports: ${reports.size}", CEN_MATCHING)

        return success()
    }

    companion object {
        const val CONTACT_COUNT_KEY = "contacts_count"
    }
}
