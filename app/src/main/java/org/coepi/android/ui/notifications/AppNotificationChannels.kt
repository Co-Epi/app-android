package org.coepi.android.ui.notifications

import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.N
import android.os.Build.VERSION_CODES.O
import androidx.annotation.RequiresApi
import org.coepi.android.R.string.infection_notification_channel_description
import org.coepi.android.R.string.infection_notification_channel_name
import org.coepi.android.system.Resources
import org.coepi.android.ui.notifications.LocalNotificationChannelId.INFECTION_REPORTS_CHANNEL

/**
 * Initializes the app's notification channels and provides their ids.
 */
class AppNotificationChannels(
    private val channels: NotificationChannelsCreator,
    private val resources: Resources
) {
    val reportsChannelId: LocalNotificationChannelId = INFECTION_REPORTS_CHANNEL

    init {
        if (SDK_INT >= O) {
            channelConfigs().forEach {
                channels.createNotificationChannel(it)
            }
        }
    }

    @RequiresApi(N)
    private fun channelConfigs(): List<NotificationChannelConfig> = listOf(
        NotificationChannelConfig(
            reportsChannelId.toString(),
            resources.getString(infection_notification_channel_name),
            resources.getString(infection_notification_channel_description),
            IMPORTANCE_DEFAULT
        )
    )
}

enum class LocalNotificationChannelId {
    INFECTION_REPORTS_CHANNEL
}
