package org.coepi.android.ble

import android.app.Application
import android.bluetooth.BluetoothAdapter
import org.coepi.android.ble.covidwatch.BLEAdvertiser
import org.coepi.android.ble.covidwatch.BLEAdvertiserImpl
import org.coepi.android.ble.covidwatch.BLEScanner
import org.coepi.android.ble.covidwatch.BLEScannerImpl
import org.coepi.android.ble.covidwatch.ScannedData
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.UUID

val bleModule = module {
    single { createBleAdvertiser(androidApplication()) }
    single { createBleScanner(androidApplication()) }
    single<BleManager> { BleManagerImpl(androidApplication(), get(), get()) }
    single<BlePreconditionsNotifier> { BlePreconditionsNotifierImpl() }
}

private val bluetoothAdapter: BluetoothAdapter? get() = BluetoothAdapter.getDefaultAdapter()

private fun createBleAdvertiser(app: Application): BLEAdvertiser =
    bluetoothAdapter?.let { adapter ->
        BLEAdvertiserImpl(app, adapter)
    } ?: NoopBleAdvertiser()

private fun createBleScanner(app: Application): BLEScanner =
    bluetoothAdapter?.let { adapter ->
        BLEScannerImpl(app, adapter)
    } ?: NoopBleScanner()

class NoopBleAdvertiser: BLEAdvertiser {
    override fun startAdvertiser(serviceUUID: UUID?, characteristicUUID: UUID?, value: String?) {}
    override fun stopAdvertiser() {}
    override fun changeAdvertisedValue(value: String?) {}
//    override fun registerIdentifierGenerator(generator: () -> UUID) {}
}

class NoopBleScanner: BLEScanner {
    override fun startScanning(serviceUUIDs: Array<UUID>?) {}
    override fun registerScanCallback(callback: (ScannedData) -> Unit) {}
    override fun stopScanning() {}
}
