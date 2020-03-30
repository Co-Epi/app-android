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
import org.coepi.android.system.log.log
import java.util.Timer
import java.util.UUID

interface BleService {
    fun configure(configuration: BleServiceConfiguration)

    fun startAdvertiser(serviceUUID: UUID, characteristicUUID: UUID, value: String)
    fun stopAdvertiser()
    fun registerAdvertiserWriteCallback(callback: (String) -> Unit)

    fun changeAdvertisedValue(value: String)
}

data class BleServiceConfiguration(
    val serviceUUID: UUID,
    val characteristicUUID: UUID,
    val startValue: String,
    val advertiser: BLEAdvertiser,
    val scanner: BLEScanner,
    val scanCallback: (String) -> Unit,
    val advertiserWriteCallback: (String) -> Unit
)

class BLEForegroundService : LifecycleService(), BleService {

    private var timer: Timer? = null

    private var configuration: BleServiceConfiguration? = null

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

        //////////////////////////////////////////////////////////////
        // TODO address this
        // NOTE: Incompatibility (temporary?) with covidwatch
        // CENs are scheduled outside, so disabling this scheduler
        //////////////////////////////////////////////////////////////
//        // scheduler a new timer to start changing the contact event numbers
//        timer?.cancel()
//        timer = Timer()
//        timer?.scheduleAtFixedRate(
//            object : TimerTask() {
//                override fun run() {
//                    configuration?.advertiser?.changeAdvertisedValue(
//                        randomUUID()
//                    )
//                }
//            },
//            MS_TO_MIN * CONTACT_EVENT_NUMBER_CHANGE_INTERVAL_MIN.toLong(),
//            MS_TO_MIN * CONTACT_EVENT_NUMBER_CHANGE_INTERVAL_MIN.toLong()
//        )
        //////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////



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

        return START_STICKY
    }


    override fun changeAdvertisedValue(value: String) {
        val configuration = configuration ?: run {
            log.e("Changing contact identifier but not configured yet")
            return
        }
        configuration.advertiser.changeAdvertisedValue(value)
    }

    override fun registerAdvertiserWriteCallback(callback: (String) -> Unit) {
        val configuration = configuration ?: error("Not configured")
        configuration.advertiser.registerWriteCallback(callback)
    }

    override fun onDestroy() {
        configuration?.advertiser?.stopAdvertiser()
        configuration?.scanner?.stopScanning()
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

    override fun configure(configuration: BleServiceConfiguration) {
        this.configuration = configuration
    }

    fun start() {
        configuration?.start() ?: error("Starting without a configuration")
    }

    private fun BleServiceConfiguration.start() {
        advertiser.registerWriteCallback(advertiserWriteCallback)
        advertiser.startAdvertiser(serviceUUID, characteristicUUID, startValue)
        scanner.registerScanCallback(scanCallback)
        scanner.startScanning(arrayOf(serviceUUID))
    }

    override fun startAdvertiser(serviceUUID: UUID, characteristicUUID: UUID, value: String) {
        configuration?.advertiser?.startAdvertiser(serviceUUID, characteristicUUID, value)
    }

    override fun stopAdvertiser() {
        configuration?.advertiser?.stopAdvertiser()
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
