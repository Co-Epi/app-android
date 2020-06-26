package org.coepi.android.ui.notifications

import android.os.Parcelable
import androidx.annotation.DrawableRes
import org.coepi.android.system.intent.IntentKey

data class NotificationConfig(
    @DrawableRes val smallIcon: Int,
    val notificationId: Int,
    val title: String,
    val text: String,
    val priority: NotificationPriority,
    val channelId: LocalNotificationChannelId,
    val intentArgs: NotificationIntentArgs
)

enum class NotificationPriority {
    DEFAULT, LOW, MIN, HIGH, MAX
}

data class NotificationIntentArgs(
    val key: IntentKey,
    val value: Parcelable
)
