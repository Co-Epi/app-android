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
import org.coepi.android.cen.Cen
import org.coepi.android.system.log.LogTag.BLE_A
import org.coepi.android.system.log.log
import java.util.Timer
import java.util.UUID

interface BleService {
    fun configure(configuration: BleServiceConfiguration)

    fun startAdvertiser(cen: Cen)
    fun stopAdvertiser()
    fun registerAdvertiserWriteCallback(callback: (Cen) -> Unit)

    fun changeAdvertisedCen(cen: Cen)
}

data class BleServiceConfiguration(
    val startCen: Cen,
    val advertiser: BLEAdvertiser,
    val scanner: BLEScanner,
    val scanCallback: (Cen) -> Unit,
    val advertiserWriteCallback: (Cen) -> Unit
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

        return START_STICKY
    }


    override fun changeAdvertisedCen(cen: Cen) {
        val configuration = configuration ?: run {
            log.e("Changing contact identifier but not configured yet", BLE_A)
            return
        }
        configuration.advertiser.changeContactEventIdentifierInServiceDataField(cen)
    }

    override fun registerAdvertiserWriteCallback(callback: (Cen) -> Unit) {
        val configuration = configuration ?: error("Not configured")
        configuration.advertiser.writeCallback = callback
    }

    override fun onDestroy() {
        configuration?.advertiser?.stopAdvertising()
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
        advertiser.writeCallback = advertiserWriteCallback
        advertiser.startAdvertising(BluetoothService.CONTACT_EVENT_SERVICE, startCen)
        scanner.callback = scanCallback
        scanner.startScanning(arrayOf(BluetoothService.CONTACT_EVENT_SERVICE))
    }

    override fun startAdvertiser(cen: Cen) {
        configuration?.advertiser?.startAdvertising(BluetoothService.CONTACT_EVENT_SERVICE, cen)
    }

    override fun stopAdvertiser() {
        configuration?.advertiser?.stopAdvertising()
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
