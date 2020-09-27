package org.coepi.android.ui.notifications

import org.coepi.android.R.drawable
import org.coepi.android.R.plurals
import org.coepi.android.R.string
import org.coepi.android.system.Resources
import org.coepi.android.system.intent.IntentKey.NOTIFICATION_REMINDER_ARGS
import org.coepi.android.system.intent.IntentNoValue
import org.coepi.android.system.log.log
import org.coepi.android.ui.notifications.NotificationPriority.HIGH

interface ReminderNotificationShower {
    fun showNotification(notificationId: Int)
    fun cancelNotification(notificationId: Int)
}

class ReminderNotificationShowerImpl(
    private val notificationsShower: NotificationsShower,
    private val notificationChannelsInitializer: AppNotificationChannels,
    private val resources: Resources
) : ReminderNotificationShower {

    override fun showNotification(notificationId: Int) {
        log.d("Showing reminder notification...")
        val title = resources.getString(string.reminder_notification_title)
        val text = resources.getString(string.reminder_notification_text)
        notificationsShower.showNotification(notificationConfiguration(notificationId, title, text))
    }

    override fun cancelNotification(notificationId: Int) {
        log.d("Canceling reminder notification $notificationId")
        notificationsShower.cancelNotification(notificationId)
    }

    private fun notificationConfiguration( notificationId: Int, title: String, text: String): NotificationConfig =
        NotificationConfig(
            drawable.ic_launcher_foreground,
            notificationId,
            title,
            text,
            HIGH,
            notificationChannelsInitializer.reportsChannelId,
            NotificationIntentArgs(NOTIFICATION_REMINDER_ARGS, IntentNoValue())
        )
}