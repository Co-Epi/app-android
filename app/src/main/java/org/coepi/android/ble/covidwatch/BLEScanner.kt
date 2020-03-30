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
import android.util.Log
import androidx.annotation.RequiresApi
import org.coepi.android.ble.covidwatch.utils.toUUID
import java.util.UUID

interface BLEScanner {
    fun startScanning(serviceUUIDs: Array<UUID>?)

    fun registerScanCallback(callback: (String) -> Unit)

    fun stopScanning()
}

data class ScannedData(val serviceUuids: List<UUID>, val serviceData: String)

class BLEScannerImpl(ctx: Context, adapter: BluetoothAdapter): BLEScanner {

    private var callback: ((String) -> Unit)? = null

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
            Log.i(TAG, "onScanFailed errorCode=$errorCode")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            Log.i(TAG, "onBatchScanResults results=$results")
            results?.forEach { handleScanResult(it) }
        }

        private fun handleScanResult(result: ScanResult) {
            val scanRecord = result.scanRecord ?: return
            val serviceUuid = this@BLEScannerImpl.serviceUuid ?: error("Didn't set service UUID")

            val contactEventIdentifier =
                scanRecord.serviceData[ParcelUuid(serviceUuid)]?.toUUID()

            if (contactEventIdentifier == null) {
                Log.i(
                    TAG,
                    "Scan result device.address=${result.device.address} RSSI=${result.rssi} CEI=N/A"
                )
                // TODO: Handle case when CEI cannot be extracted from scan record.
            } else {
                Log.i(
                    TAG,
                    "Scan result device.address=${result.device.address} RSSI=${result.rssi} CEI=${contactEventIdentifier.toString()
                        .toUpperCase()}"
                )

                scanRecord.serviceUuids.filter { it.uuid == serviceUuid }.forEach { uuid ->
                    scanRecord.serviceData[uuid]?.let { bytes ->
                        callback?.invoke(String(bytes))
                    }
                }
            }
        }
    }

    // CONSTANTS
    companion object {
        private const val TAG = "BLEScanner"
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun startScanning(serviceUUIDs: Array<UUID>?) {

        val scanner = scanner ?: return

        val scanFilters = serviceUUIDs?.map {
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
        Log.i(TAG, "Started scanning")
    }

    override fun registerScanCallback(callback: (String) -> Unit) {
        this.callback = callback
    }

    override fun stopScanning() {
        scanner?.stopScan(scanCallback)
        Log.i(TAG, "Stopped scanning")
    }
}
