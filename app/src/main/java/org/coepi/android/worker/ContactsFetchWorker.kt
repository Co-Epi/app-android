package org.coepi.android.worker

import android.content.Context
import androidx.work.ListenableWorker.Result.success
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single
import io.reactivex.Single.create
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
) : RxWorker(appContext, workerParams), KoinComponent {

    private val coEpiRepo: CoEpiRepo by inject()
    private val reportsDao: RealmCenReportDao by inject()

    override fun createWork(): Single<Result> = create<Result> { emitter ->
        emitter.onSuccess(doWork())
    }

    private fun doWork(): Result {
        log.i("Contacts fetch worker started...", CEN_MATCHING)

        val reportsResult = coEpiRepo.reports()
        val reports: List<ReceivedCenReport> = reportsResult.successOrNull() ?: emptyList()

        reports.forEach {
            reportsDao.insert(it.report)
        }

        log.i("Contacts fetch worker finished. Saved reports: ${reports.size}", CEN_MATCHING)
        return success()
    }
}
