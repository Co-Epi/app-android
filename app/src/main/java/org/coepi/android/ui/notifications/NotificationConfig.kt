package org.coepi.android.ui.notifications

import androidx.annotation.DrawableRes

data class NotificationConfig(
    @DrawableRes val smallIcon: Int,
    val title: String,
    val text: String,
    val priority: NotificationPriority,
    val channelId: LocalNotificationChannelId
)

enum class NotificationPriority {
    DEFAULT, LOW, MIN, HIGH, MAX
}
