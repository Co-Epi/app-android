package org.coepi.android.ble

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.coepi.android.ble.covidwatch.BLEAdvertiser
import org.coepi.android.ble.covidwatch.BLEForegroundService
import org.coepi.android.ble.covidwatch.BLEForegroundService.LocalBinder
import org.coepi.android.ble.covidwatch.BLEScanner

class BleManager(val app: Application, val advertiser: BLEAdvertiser, val scanner: BLEScanner) {

    private val intent = Intent(app, BLEForegroundService::class.java)

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val bleService = (service as LocalBinder).service
            bleService.advertiser = advertiser
            bleService.scanner = scanner
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    fun startService() {
        app.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        app.startService(intent)
    }

    fun stopService() {
        app.stopService(intent)
    }
}
