package org.coepi.android.worker.tcnfetcher

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.log.LogTag.TCN_MATCHING
import org.coepi.android.system.log.log
import org.koin.core.KoinComponent
import org.koin.core.inject

class ContactsFetchWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams), KoinComponent {

    private val alertsRepo: AlertsRepo by inject()

    override suspend fun doWork(): Result {
        log.d("Contacts fetch worker started.", TCN_MATCHING)
        alertsRepo.requestUpdateReports()
        log.d("Contacts fetch worker finished.", TCN_MATCHING)
        return success()
    }
}
