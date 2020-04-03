package org.coepi.android.ble

import android.app.Application
import android.bluetooth.BluetoothAdapter
import org.coepi.android.ble.covidwatch.BLEAdvertiser
import org.coepi.android.ble.covidwatch.BLEAdvertiserImpl
import org.coepi.android.ble.covidwatch.BLEScanner
import org.coepi.android.ble.covidwatch.BLEScannerImpl
import org.coepi.android.cen.Cen
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.UUID

val bleModule = module {
    single<BleManager> { BleManagerImpl(androidApplication()) }
}

val bleSimulatorModule = module {
    single<BleManager> { BleSimulator() }
}

val bluetoothAdapter: BluetoothAdapter? get() = BluetoothAdapter.getDefaultAdapter()

private fun createBleAdvertiser(app: Application): BLEAdvertiser =
    bluetoothAdapter?.let { adapter ->
        BLEAdvertiserImpl(app, adapter)
    } ?: NoopBleAdvertiser()

private fun createBleScanner(app: Application): BLEScanner =
    bluetoothAdapter?.let { adapter ->
        BLEScannerImpl(app, adapter)
    } ?: NoopBleScanner()

class NoopBleAdvertiser: BLEAdvertiser {
    override fun startAdvertiser(serviceUUID: UUID, characteristicUUID: UUID, cen: Cen) {}
    override fun stopAdvertiser() {}
    override fun changeAdvertisedValue(cen: Cen) {}
    override fun registerWriteCallback(callback: (Cen) -> Unit) {}
}

class NoopBleScanner: BLEScanner {
    override fun startScanning(serviceUUIDs: Array<UUID>) {}
    override fun registerScanCallback(callback: (Cen) -> Unit) {}
    override fun stopScanning() {}
}
