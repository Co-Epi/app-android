package org.coepi.android.worker.cenfetcher

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequest.Builder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit.MINUTES

class ContactsFetchManager(context: Context) {

    val workName = "contacts_fetch_worker"

    init {
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(workName, REPLACE, createWorkerRequest())
    }

    private fun createWorkerRequest(): PeriodicWorkRequest {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(CONNECTED)
            .build()

        return Builder(ContactsFetchWorker::class.java, 15, MINUTES)
//            .setInitialDelay(1, SECONDS) // If using BLE simulator, ensure it can store keys first
            .setConstraints(constraints)
            .build()
    }
}
