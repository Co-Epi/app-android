package org.coepi.android.ble

import android.app.Application
import android.app.Notification
import android.app.Notification.CATEGORY_SERVICE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.MainActivity
import org.coepi.android.R.drawable.ic_launcher_foreground
import org.coepi.android.cen.Cen
import org.coepi.android.cen.MyCenProvider
import org.coepi.android.system.log.LogTag.BLE
import org.coepi.android.system.log.log
import org.tcncoalition.tcnclient.bluetooth.BluetoothStateListener
import org.tcncoalition.tcnclient.bluetooth.TcnBluetoothService
import org.tcncoalition.tcnclient.bluetooth.TcnBluetoothService.LocalBinder
import org.tcncoalition.tcnclient.bluetooth.TcnBluetoothServiceCallback

interface BleManager {
    val observedCens: Observable<Cen>

    fun startService()
    fun stopService()
}

class BleManagerImpl(
    private val app: Application,
    private val myCenProvider: MyCenProvider
): BleManager, BluetoothStateListener {

    override val observedCens: PublishSubject<Cen> = create()

    private val intent get() = Intent(app, TcnBluetoothService::class.java)

    private var service: TcnBluetoothService? = null

    inner class BluetoothServiceCallback : TcnBluetoothServiceCallback {
        override fun generateTcn(): ByteArray =
            myCenProvider.generateCen().bytes

        override fun onTcnFound(tcn: ByteArray, estimatedDistance: Double?) {
            observedCens.onNext(Cen(tcn))
        }
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@BleManagerImpl.service = (service as LocalBinder).service.apply {
                val notification = foregroundNotification()
                startForegroundNotificationIfNeeded(NOTIFICATION_ID, notification)
                setBluetoothStateListener(this@BleManagerImpl)
                startTcnExchange(BluetoothServiceCallback())
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    private fun foregroundNotification(): Notification {
        createNotificationChannelIfNeeded()

        val notificationIntent = Intent(app, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            app, 0, notificationIntent, FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(app, CHANNEL_ID)
            .setContentTitle("CoEpi is running")
            .setSmallIcon(ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setCategory(CATEGORY_SERVICE)
            .build()
    }

    /**
     * This notification channel is only required for android versions above
     * android O. This creates the necessary notification channel for the foregroundService
     * to function.
     */
    private fun createNotificationChannelIfNeeded() {
        if (SDK_INT >= O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager? = ContextCompat.getSystemService(
                app, NotificationManager::class.java
            )
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun startService() {
        app.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        app.startService(intent)
    }

    override fun stopService() {
        app.stopService(intent)
    }

    companion object {
        private const val CHANNEL_ID = "CoEpiBluetoothContactChannel"
        const val NOTIFICATION_ID = 1
    }

    override fun bluetoothStateChanged(bluetoothOn: Boolean) {
        log.i("Bluetooth state changed, on: $bluetoothOn", BLE)
    }
}
