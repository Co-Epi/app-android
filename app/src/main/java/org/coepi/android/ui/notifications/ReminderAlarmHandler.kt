package org.coepi.android.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.coepi.android.system.log.log
import org.koin.core.KoinComponent
import org.koin.core.inject

class ReminderAlarmHandler : BroadcastReceiver(), KoinComponent {

    private val reminderNotificationShower: ReminderNotificationShower by inject()
    internal val injectedContext: Context by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        val info : Int? = intent?.getIntExtra("code",0)
        log.d("[Reminder] received $info")
        reminderNotificationShower.showNotification(info ?: 0)
    }

}

