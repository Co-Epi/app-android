package org.coepi.android.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi

class NotificationChannelsCreator(private val context: Context) {

    @RequiresApi(VERSION_CODES.O)
    fun createNotificationChannel(config: NotificationChannelConfig) {
        val channel = NotificationChannel(
            config.id.toString(), config.name,
            config.importance
        ).apply {
            description = config.description
        }
        val notificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

data class NotificationChannelConfig(
    val id: String,
    val name: String,
    val description: String,
    val importance: Int
)
