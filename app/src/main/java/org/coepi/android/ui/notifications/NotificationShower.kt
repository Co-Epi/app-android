package org.coepi.android.ui.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
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

    fun showNotification(config: NotificationConfig) {
        with(NotificationManagerCompat.from(context)) {
            notify(config.notificationId, notificationBuilder(config).build())
        }
    }

    /**
     * TODO: need a way to cancel specific system notifications using the unique notificationId that's
     * apart of the [NotificationConfig]
     */
    fun cancelNotification(notificationId: Int) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    fun cancelAllNotifications() {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun isShowingNotifications(): Boolean {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.activeNotifications.isNotEmpty()
    }

    private fun pendingIntent(args: NotificationIntentArgs): PendingIntent =
        PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java).apply {
                flags = FLAG_ACTIVITY_SINGLE_TOP
                putExtra(args.key.toString(), args.value)
            }, FLAG_UPDATE_CURRENT
        )

    private fun notificationBuilder(config: NotificationConfig): Builder =
        Builder(context, channelId)
            .setSmallIcon(config.smallIcon)
            .setContentTitle(config.title)
            .setContentText(config.text)
            .setPriority(config.priority.toInt())
            .setContentIntent(pendingIntent(config.intentArgs))
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
