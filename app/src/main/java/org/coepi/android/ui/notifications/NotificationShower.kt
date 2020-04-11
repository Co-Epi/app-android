package org.coepi.android.ui.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.core.app.NotificationCompat.PRIORITY_MAX
import androidx.core.app.NotificationCompat.PRIORITY_MIN
import androidx.core.app.NotificationManagerCompat
import org.coepi.android.MainActivity
import org.coepi.android.ui.notifications.NotificationPriority.DEFAULT
import org.coepi.android.ui.notifications.NotificationPriority.HIGH
import org.coepi.android.ui.notifications.NotificationPriority.LOW
import org.coepi.android.ui.notifications.NotificationPriority.MAX
import org.coepi.android.ui.notifications.NotificationPriority.MIN

class NotificationsShower(
    private val context: Context
) {
    private val channelId: String = "infection_contact_report"
    private val notificationId: Int = 0

    fun showNotification(config: NotificationConfig) {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, notificationBuilder(config).build())
        }
    }

    private fun pendingIntent(): PendingIntent = PendingIntent.getActivity(context, 0,
        Intent(context, MainActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        }, 0)

    private fun notificationBuilder(config: NotificationConfig): Builder = Builder(context, channelId)
        .setSmallIcon(config.smallIcon)
        .setContentTitle(config.title)
        .setContentText(config.text)
        .setPriority(config.priority.toInt())
        .setContentIntent(pendingIntent())
        .setChannelId(config.channelId.toString())
        .setAutoCancel(true)
}

private fun NotificationPriority.toInt() = when (this) {
    DEFAULT -> PRIORITY_DEFAULT
    LOW -> PRIORITY_LOW
    MIN -> PRIORITY_MIN
    HIGH -> PRIORITY_HIGH
    MAX -> PRIORITY_MAX
}
