package org.coepi.android.ui.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.coepi.android.system.log.log

public fun ReminderAlarmHandler.cancelReminderWith(id: Int){
    val alarmManager = injectedContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(injectedContext, ReminderAlarmHandler::class.java)
    val pendingIntent = PendingIntent.getBroadcast(injectedContext,id,intent, PendingIntent.FLAG_UPDATE_CURRENT)
    log.d("[Reminder] cancelling notification with id: $id")
    pendingIntent.cancel()
    alarmManager.cancel(pendingIntent)
}