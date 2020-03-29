package org.coepi.android.ble.covidwatch

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import org.coepi.android.MainActivity
import org.coepi.android.R
import org.coepi.android.ble.covidwatch.utils.UUIDs
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

class BLEForegroundService : LifecycleService() {

    private var timer: Timer? = null

    var advertiser: BLEAdvertiser? = null
    var scanner: BLEScanner? = null

    companion object {
        // CONSTANTS
        private const val CHANNEL_ID = "CovidBluetoothContactChannel"
        private const val CONTACT_EVENT_NUMBER_CHANGE_INTERVAL_MIN = 15
        private const val MS_TO_MIN = 60000
        private const val TAG = "BLEForegroundService"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        createNotificationChannelIfNeeded()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tags is logging")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(6, notification)

        // scheduler a new timer to start changing the contact event numbers
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    advertiser?.changeContactEventIdentifierInServiceDataField(UUID.randomUUID())
                }
            },
            MS_TO_MIN * CONTACT_EVENT_NUMBER_CHANGE_INTERVAL_MIN.toLong(),
            MS_TO_MIN * CONTACT_EVENT_NUMBER_CHANGE_INTERVAL_MIN.toLong()
        )

        val newContactEventUUID = UUID.randomUUID()
        // TODO
//        CovidWatchDatabase.databaseWriteExecutor.execute {
//            val dao: ContactEventDAO = CovidWatchDatabase.getInstance(this).contactEventDAO()
//            val contactEvent = ContactEvent(newContactEventUUID.toString())
//            val isCurrentUserSick = getSharedPreferences(
//                getString(R.string.preference_file_key),
//                Context.MODE_PRIVATE
//            ).getBoolean(getString(R.string.preference_is_current_user_sick), false)
//            contactEvent.wasPotentiallyInfectious = isCurrentUserSick
//            dao.insert(contactEvent)
//        }
        advertiser?.startAdvertiser(UUIDs.CONTACT_EVENT_SERVICE, newContactEventUUID)
        scanner?.startScanning(arrayOf<UUID>(UUIDs.CONTACT_EVENT_SERVICE))

        return START_STICKY
    }

    override fun onDestroy() {
        advertiser?.stopAdvertiser()
        scanner?.stopScanning()
        timer?.apply {
            cancel()
            purge()
        }
        super.onDestroy()
    }

    inner class LocalBinder : Binder() {
        internal val service: BLEForegroundService
            get() = this@BLEForegroundService
    }

    private val binder: IBinder = LocalBinder()

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return binder
    }

    /**
     * This notification channel is only required for android versions above
     * android O. This creates the necessary notification channel for the foregroundService
     * to function.
     */
    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

}