package org.coepi.android.ble.covidwatch

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import androidx.annotation.RequiresApi
import org.coepi.android.ble.covidwatch.utils.toUUID
import org.coepi.android.cen.Cen
import org.coepi.android.system.log.LogTag.BLE_S
import org.coepi.android.system.log.log
import java.util.UUID

interface BLEScanner {
    fun startScanning(serviceUUIDs: Array<UUID>)

    fun registerScanCallback(callback: (Cen) -> Unit)

    fun stopScanning()
}

class BLEScannerImpl(ctx: Context, adapter: BluetoothAdapter): BLEScanner {

    private var callback: ((Cen) -> Unit)? = null

    // TODO consider injecting UUID so it's not optional
    private var serviceUuid: UUID? = null

    // BLE
    private val scanner: BluetoothLeScanner? = adapter.bluetoothLeScanner

    // CONTEXT
    var context: Context = ctx

    // CALLBACKS
    private var scanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            log.e("onScanFailed errorCode=$errorCode", BLE_S)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            log.i("onBatchScanResults results=$results", BLE_S)
            results?.forEach { handleScanResult(it) }
        }

        private fun handleScanResult(result: ScanResult) {
            val scanRecord = result.scanRecord ?: return
            val serviceUuid = this@BLEScannerImpl.serviceUuid ?: error("Didn't set service UUID")

            val contactEventIdentifier =
                scanRecord.serviceData[ParcelUuid(serviceUuid)]?.toUUID()

            if (contactEventIdentifier == null) {
                log.i("Scan result device.address=${result.device.address} RSSI=${result.rssi} CEI=N/A", BLE_S)
                // TODO: Handle case when CEI cannot be extracted from scan record.
            } else {
                log.i("Scan result device.address=${result.device.address} RSSI=${result.rssi} " +
                        "CEI=${contactEventIdentifier.toString().toUpperCase()}", BLE_S)

                scanRecord.serviceUuids.filter { it.uuid == serviceUuid }.forEach { uuid ->
                    scanRecord.serviceData[uuid]?.let { bytes ->
                        callback?.invoke(Cen(bytes))
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun startScanning(serviceUUIDs: Array<UUID>) {

        val scanner = scanner ?: return

        val scanFilters = serviceUUIDs.map {
            ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build()
        }

        // we use low power scan mode to conserve battery,
        // CALLBACK_TYPE_ALL_MATCHES will run the callback for every discovery
        // instead of batching them up. MATCH_MODE_AGGRESSIVE will try to connect
        // even with 1 advertisement.
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(1000)
            .build()

        // The scan filter is incredibly important to allow android to run scans
        // in the background
        scanner.startScan(scanFilters, scanSettings, scanCallback)
        log.i("Started scanning", BLE_S)
    }

    override fun registerScanCallback(callback: (Cen) -> Unit) {
        this.callback = callback
    }

    override fun stopScanning() {
        scanner?.stopScan(scanCallback)
        log.i("Stopped scanning", BLE_S)
    }
}
