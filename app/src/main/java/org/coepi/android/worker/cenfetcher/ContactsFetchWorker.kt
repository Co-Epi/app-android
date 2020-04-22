package org.coepi.android.worker.cenfetcher

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import org.coepi.android.cen.CenReportDao
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
    private val reportsDao: CenReportDao by inject()

    override suspend fun doWork(): Result {
        log.d("Contacts fetch worker started...", CEN_MATCHING)

        val reportsResult = coEpiRepo.reports()
        val reports: List<ReceivedCenReport> = reportsResult.successOrNull() ?: emptyList()

        val insertedCount = reports.map {
            reportsDao.insert(it.report)
        }.filter { it }.size

        // TODO only during early testing. Comment / remove
        val currentReportsCount = reportsDao.all().size
        log.d("Report count in DB: $currentReportsCount")

        setProgress(workDataOf(CONTACT_COUNT_KEY to insertedCount))
        // TODO why is this need?
        // TODO without delay, live data never receives the progress
        delay(100)

        log.i("Contacts fetch worker finished. Saved new reports: $insertedCount", CEN_MATCHING)

        return success()
    }

    companion object {
        const val CONTACT_COUNT_KEY = "contacts_count"
    }
}
