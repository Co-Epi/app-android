package org.coepi.android.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType.CONNECTED
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequest.Builder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit.MINUTES

class ContactsFetchManager(context: Context) {

    init {
        WorkManager.getInstance(context).enqueue(createWorkerRequest())
    }

    private fun createWorkerRequest(): PeriodicWorkRequest {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(CONNECTED)
            .build()

        return Builder(ContactsFetchWorker::class.java, 15, MINUTES)
            .setConstraints(constraints)
            .build()
    }
}
