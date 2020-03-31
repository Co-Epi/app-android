package org.coepi.android.ble

import android.app.Application
import android.bluetooth.BluetoothAdapter
import org.coepi.android.ble.covidwatch.BLEAdvertiser
import org.coepi.android.ble.covidwatch.BLEAdvertiserImpl
import org.coepi.android.ble.covidwatch.BLEScanner
import org.coepi.android.ble.covidwatch.BLEScannerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.UUID

val bleModule = module {
    single<BlePreconditionsNotifier> { BlePreconditionsNotifierImpl() }
    single { createBleAdvertiser(androidApplication()) }
    single { createBleScanner(androidApplication()) }
    single<BleManager> { BleManagerImpl(androidApplication(), get(), get()) }
}

val bleSimulatorModule = module {
    single<BlePreconditionsNotifier> { BlePreconditionsNotifierImpl() }
    single<BleManager> { BleSimulator() }
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
    override fun startAdvertiser(serviceUUID: UUID, characteristicUUID: UUID, value: String) {}
    override fun stopAdvertiser() {}
    override fun changeAdvertisedValue(value: String) {}
    override fun registerWriteCallback(callback: (String) -> Unit) {}
}

class NoopBleScanner: BLEScanner {
    override fun startScanning(serviceUUIDs: Array<UUID>) {}
    override fun registerScanCallback(callback: (String) -> Unit) {}
    override fun stopScanning() {}
}
