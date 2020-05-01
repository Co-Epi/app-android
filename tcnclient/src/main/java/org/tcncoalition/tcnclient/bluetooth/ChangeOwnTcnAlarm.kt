package org.tcncoalition.tcnclient.bluetooth

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.SystemClock
import org.tcncoalition.tcnclient.TcnConstants
import org.tcncoalition.tcnclient.receiver.ChangeOwnTcnReceiver
import java.util.concurrent.TimeUnit

class ChangeOwnTcnAlarm {
    private var pendingIntent: PendingIntent? = null
    private var alarmManager: AlarmManager? = null

    fun schedule(context: Context, alarmManager: AlarmManager) {
        this.alarmManager = alarmManager
        this.pendingIntent = ChangeOwnTcnReceiver.pendingIntent(context)

        val nextAlarmTime =
            SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(TcnConstants.TCN_CHANGE_PERIOD)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            nextAlarmTime,
            pendingIntent
        )
    }

    fun cancel() {
        pendingIntent?.let { alarmManager?.cancel(it) }
    }
}