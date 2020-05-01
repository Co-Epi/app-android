package org.tcncoalition.tcnclient.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.tcncoalition.tcnclient.TcnClient

class ChangeOwnTcnReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        TcnClient.tcnManager?.changeOwnTcn()
    }

    companion object {
        private const val REQUEST_CODE = 42

        fun pendingIntent(context: Context): PendingIntent {
            val intent = Intent(context, ChangeOwnTcnReceiver::class.java)
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)

            return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}