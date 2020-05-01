package org.tcncoalition.tcnclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.app.NotificationCompat
import org.tcncoalition.tcnclient.bluetooth.BluetoothStateListener
import org.tcncoalition.tcnclient.bluetooth.TcnBluetoothService
import org.tcncoalition.tcnclient.bluetooth.TcnBluetoothServiceCallback

abstract class TcnManager(
    private val context: Context
) : BluetoothStateListener,
    TcnBluetoothServiceCallback {

    protected var service: TcnBluetoothService? = null
    private var isBound = false

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            this@TcnManager.service =
                (service as TcnBluetoothService.LocalBinder).service.apply {
                    val notification = foregroundNotification().build()
                    startForegroundNotificationIfNeeded(NOTIFICATION_ID, notification)
                    setBluetoothStateListener(this@TcnManager)
                    startTcnExchange(this@TcnManager)
                }
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    abstract fun foregroundNotification(): NotificationCompat.Builder

    fun startService() {
        context.bindService(
            Intent(context, TcnBluetoothService::class.java),
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    fun stopService() {
        if (isBound) {
            service?.stopTcnExchange()
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    fun changeOwnTcn() {
        if (isBound) {
            service?.changeOwnTcn()
        } else {
            startService()
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1 // Don't use 0
    }
}