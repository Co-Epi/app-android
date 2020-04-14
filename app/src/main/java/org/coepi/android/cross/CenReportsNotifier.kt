package org.coepi.android.cross

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import org.coepi.android.R.drawable.ic_launcher_foreground
import org.coepi.android.R.plurals.alerts_new_notifications_count
import org.coepi.android.R.string.infection_notification_title
import org.coepi.android.system.Resources
import org.coepi.android.system.intent.IntentNoValue
import org.coepi.android.system.intent.IntentKey.NOTIFICATION_INFECTION_ARGS
import org.coepi.android.system.log.log
import org.coepi.android.ui.notifications.AppNotificationChannels
import org.coepi.android.ui.notifications.NotificationConfig
import org.coepi.android.ui.notifications.NotificationIntentArgs
import org.coepi.android.ui.notifications.NotificationPriority.HIGH
import org.coepi.android.ui.notifications.NotificationsShower
import org.coepi.android.worker.cenfetcher.ContactsFetchManager
import org.coepi.android.worker.cenfetcher.ContactsFetchWorker.Companion.CONTACT_COUNT_KEY

class CenReportsNotifier(
    private val notificationsShower: NotificationsShower,
    private val resources: Resources,
    private val notificationChannelsInitializer: AppNotificationChannels,
    private val context: Context,
    private val contactsFetchManager: ContactsFetchManager
) {

    fun attach(lifecycleOwner: LifecycleOwner) {
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(contactsFetchManager.workName)
            .observe(lifecycleOwner, Observer { workInfos ->
                workInfos?.let {
                    workInfos.forEach {
                        handleWorkInfo(it)
                    }
                }
            })
    }

    private fun handleWorkInfo(workInfo: WorkInfo) {
        val progress = workInfo.progress
        val count = progress.getInt(CONTACT_COUNT_KEY, -1)

        if (count >= 0) {
            log.d("Worker returned $count new reports")
        }

        if (count > 0) {
            log.d("Showing notification...")
            notificationsShower.showNotification(notificationConfiguration(count))
        }
    }

    private fun notificationConfiguration(count: Int): NotificationConfig = NotificationConfig(
        ic_launcher_foreground,
        resources.getString(infection_notification_title),
        resources.getQuantityString(alerts_new_notifications_count, count),
        HIGH,
        notificationChannelsInitializer.reportsChannelId,
        NotificationIntentArgs(NOTIFICATION_INFECTION_ARGS, IntentNoValue())
    )
}
