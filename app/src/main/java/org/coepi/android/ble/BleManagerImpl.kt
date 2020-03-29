package org.coepi.android.ble

import android.app.Application
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.ble.covidwatch.BLEAdvertiser
import org.coepi.android.ble.covidwatch.BLEForegroundService
import org.coepi.android.ble.covidwatch.BLEForegroundService.LocalBinder
import org.coepi.android.ble.covidwatch.BLEScanner
import org.coepi.android.ble.covidwatch.BleServiceConfiguration
import java.util.UUID

interface BleManager {
    val scanObservable: Observable<UUID>

    fun startService()
    fun stopService()

    fun changeContactEventIdentifierInServiceDataField(identifier: UUID)
}

class BleManagerImpl(val app: Application, val advertiser: BLEAdvertiser, val scanner: BLEScanner)
    : BleManager {

    override val scanObservable: PublishSubject<UUID> = create()

    private val intent get() = Intent(app, BLEForegroundService::class.java)

    private var service: BLEForegroundService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val bleService = (service as LocalBinder).service
            bleService.configure(BleServiceConfiguration(
                advertiser,
                scanner,
                scanCallback = {
                    scanObservable.onNext(it)
                }
            ))

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
