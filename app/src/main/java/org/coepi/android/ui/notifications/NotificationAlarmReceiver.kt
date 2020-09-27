package org.coepi.android.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import org.coepi.android.R.drawable
import org.coepi.android.R.plurals
import org.coepi.android.R.string
import org.coepi.android.system.Resources
import org.coepi.android.system.intent.IntentKey.NOTIFICATION_INFECTION_ARGS
import org.coepi.android.system.intent.IntentNoValue
import org.coepi.android.system.log.log
import org.coepi.android.ui.notifications.NotificationPriority.HIGH
import org.koin.core.KoinComponent
import org.koin.core.inject

class NotificationAlarmReceiver(
) : BroadcastReceiver(), KoinComponent {

    private val reminderNotificationShower: ReminderNotificationShower by inject()
//    private val notificationChannelsInitializer: AppNotificationChannels by inject()
//    private val resources: Resources by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        val info : Int? = intent?.getIntExtra("code",0)
        log.d("[Reminder] received $info")
//        Toast.makeText(context, "Alarm triggered $info",
//            Toast.LENGTH_LONG).show()
        reminderNotificationShower.showNotification(info ?: 0)
    }

}