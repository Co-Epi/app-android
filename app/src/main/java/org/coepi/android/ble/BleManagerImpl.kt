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
import java.util.UUID.fromString

interface BleManager {
    val scanObservable: Observable<String>

    fun startAdvertiser(value: String)
    fun stopAdvertiser()

    fun startService(value: String)
    fun stopService()

    fun changeAdvertisedValue(value: String)
}

class BleManagerImpl(
    private val app: Application,
    private val advertiser: BLEAdvertiser,
    private val scanner: BLEScanner
) : BleManager {

    private val serviceUUID: UUID = fromString("0000C019-0000-1000-8000-00805F9B34FB")
    private val characteristicUUID: UUID = fromString("D61F4F27-3D6B-4B04-9E46-C9D2EA617F62")

    override val scanObservable: PublishSubject<String> = create()

    private val intent get() = Intent(app, BLEForegroundService::class.java)

    private var service: BLEForegroundService? = null

    private var value: String? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val value = this@BleManagerImpl.value ?: error("No value to advertise")
            this@BleManagerImpl.service = (service as LocalBinder).service.apply {
                configureAndStart(value)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    private fun BLEForegroundService.configureAndStart(value: String) {
        configure(BleServiceConfiguration(
            serviceUUID, characteristicUUID, value, advertiser, scanner,
            scanCallback = {
                scanObservable.onNext(it)
            },
            advertiserWriteCallback = {
                // Since advertiser write is used as scan replacement
                // for the Android (Central) / iOS (Peripheral) case, we broadcast it as scan.
                scanObservable.onNext(it)
            }
        ))
        start()
    }

    override fun changeAdvertisedValue(value: String) {
        this.value = value

        service?.changeAdvertisedValue(value)
    }

    override fun startAdvertiser(value: String) {
        this.value = value

        service?.startAdvertiser(serviceUUID, characteristicUUID, value)
    }

    override fun startService(value: String) {
        this.value = value

        app.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        app.startService(intent)
    }

    override fun stopAdvertiser() {
        service?.stopAdvertiser()
    }

    override fun stopService() {
        app.stopService(intent)
    }
}
