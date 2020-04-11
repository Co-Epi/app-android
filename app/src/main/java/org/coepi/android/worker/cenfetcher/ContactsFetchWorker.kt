package org.coepi.android.worker.cenfetcher

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import org.coepi.android.cen.RealmCenReportDao
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.common.successOrNull
import org.coepi.android.repo.CoEpiRepo
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

    override suspend fun doWork(): Result {
        log.i("Contacts fetch worker started...", CEN_MATCHING)

        val reportsResult = coEpiRepo.reports()
        val reports: List<ReceivedCenReport> = reportsResult.successOrNull() ?: emptyList()

        reports.forEach {
            reportsDao.insert(it.report)
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
