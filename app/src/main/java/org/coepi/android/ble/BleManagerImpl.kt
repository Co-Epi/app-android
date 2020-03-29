package org.coepi.android.ble

import android.app.Application
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.coepi.android.ble.covidwatch.BLEAdvertiser
import org.coepi.android.ble.covidwatch.BLEForegroundService
import org.coepi.android.ble.covidwatch.BLEForegroundService.LocalBinder
import org.coepi.android.ble.covidwatch.BLEScanner
import java.util.UUID

interface BleManager {
    fun startService()
    fun stopService()

    fun changeContactEventIdentifierInServiceDataField(identifier: UUID)
}

class BleManagerImpl(val app: Application, val advertiser: BLEAdvertiser, val scanner: BLEScanner)
    : BleManager {

    private val intent get() = Intent(app, BLEForegroundService::class.java)

    private var service: BLEForegroundService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val bleService = (service as LocalBinder).service

            bleService.advertiser = advertiser
            bleService.scanner = scanner

            this@BleManagerImpl.service = bleService
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    override fun changeContactEventIdentifierInServiceDataField(identifier: UUID) {
        service?.changeContactEventIdentifierInServiceDataField(identifier)
    }

    override fun startService() {
        app.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        app.startService(intent)
    }

    override fun stopService() {
        app.stopService(intent)
    }
}
