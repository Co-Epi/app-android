package org.tcncoalition.tcnclient.bluetooth

import android.app.AlarmManager
import android.app.Notification
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log

class TcnBluetoothService : Service() {

    private var tcnBluetoothManager: TcnBluetoothManager? = null
    private var bluetoothStateListener: BluetoothStateListener? = null
    private val binder: IBinder = LocalBinder()
    private var isStarted = false

    private val changeOwnTcn = ChangeOwnTcnAlarm()

    override fun onDestroy() {
        tcnBluetoothManager?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent) = binder

    fun startForegroundNotificationIfNeeded(id: Int, notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(id, notification)
        }
    }

    private fun BluetoothAdapter.supportsAdvertising() =
        isMultipleAdvertisementSupported && bluetoothLeAdvertiser != null

    fun setBluetoothStateListener(bluetoothStateListener: BluetoothStateListener) {
        this.bluetoothStateListener = bluetoothStateListener
    }

    private val bluetoothStateReceiver = BluetoothStateReceiver() { bluetoothOn ->
        if (bluetoothOn) {
            tcnBluetoothManager?.start()
        } else {
            tcnBluetoothManager?.stop()
        }
        bluetoothStateListener?.bluetoothStateChanged(bluetoothOn)
    }

    fun startTcnExchange(tcnCallback: TcnBluetoothServiceCallback) {
        if (isStarted) return
        changeOwnTcn.schedule(this, getSystemService(Context.ALARM_SERVICE) as AlarmManager)

        isStarted = true

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.supportsAdvertising()) return

        val scanner = bluetoothAdapter.bluetoothLeScanner ?: return
        val advertiser = bluetoothAdapter.bluetoothLeAdvertiser ?: return

        tcnBluetoothManager = TcnBluetoothManager(
            this@TcnBluetoothService,
            scanner,
            advertiser,
            tcnCallback
        )

        tcnBluetoothManager?.start()

        registerReceiver(
            bluetoothStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
    }

    fun changeOwnTcn() {
        tcnBluetoothManager?.changeOwnTcn()
        changeOwnTcn.schedule(this, getSystemService(Context.ALARM_SERVICE) as AlarmManager)
    }

    fun stopTcnExchange() {
        if (!isStarted) return
        Log.i(TcnBluetoothService::javaClass.name, "stopTcnExchange")
        isStarted = false
        unregisterReceiver(bluetoothStateReceiver)
        tcnBluetoothManager?.stop()
        changeOwnTcn.cancel()
    }

    inner class LocalBinder : Binder() {
        val service = this@TcnBluetoothService
    }
}
