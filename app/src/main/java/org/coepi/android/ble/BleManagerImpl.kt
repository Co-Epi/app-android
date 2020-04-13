package org.coepi.android.ble

import android.app.Application
import android.bluetooth.BluetoothAdapter
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
import org.coepi.android.cen.Cen
import org.coepi.android.system.log.LogTag.BLE
import org.coepi.android.system.log.log

interface BleManager {
    val observedCens: Observable<Cen>

    fun startAdvertiser(cen: Cen)
    fun stopAdvertiser()

    fun startService(cen: Cen)
    fun stopService()

    fun changeAdvertisedValue(cen: Cen)
}

class BleManagerImpl(
    private val app: Application
) : BleManager {

    override val observedCens: PublishSubject<Cen> = create()

    private val intent get() = Intent(app, BLEForegroundService::class.java)

    private var service: BLEForegroundService? = null

    private var cen: Cen? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val value = this@BleManagerImpl.cen ?: error("No value to advertise")
            this@BleManagerImpl.service = (service as LocalBinder).service.apply {
                configureAndStart(value)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    private fun BluetoothAdapter.supportsAdvertising() =
        isMultipleAdvertisementSupported && bluetoothLeAdvertiser != null

    private fun BLEForegroundService.configureAndStart(cen: Cen) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            log.e("Bluetooth adapter is null. Can't continue", BLE)
            return
        }

        configure(BleServiceConfiguration(
            cen,
            takeIf { bluetoothAdapter.supportsAdvertising() }?.let {
                BLEAdvertiser(app, bluetoothAdapter)
            },
            BLEScanner(app, bluetoothAdapter),
            scanCallback = {
                observedCens.onNext(it)
            },
            advertiserWriteCallback = {
                // Since advertiser write is used as scan replacement
                // for the Android (Central) / iOS (Peripheral) case, we broadcast it as scan.
                observedCens.onNext(it)
            }
        ))

        log.i("Staring BLE advertising CEN: $cen")
        log.i("NOTE: BLE advertising/scanning logs currently not in UI, since covidwatch " +
                "doesn't use our logger", BLE)
        start()
    }

    override fun changeAdvertisedValue(cen: Cen) {
        this.cen = cen

        service?.changeAdvertisedCen(cen)
    }

    override fun startAdvertiser(cen: Cen) {
        this.cen = cen

        service?.startAdvertiser(cen)
    }

    override fun startService(cen: Cen) {
        this.cen = cen

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
