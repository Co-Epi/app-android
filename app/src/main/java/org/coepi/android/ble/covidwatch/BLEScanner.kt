package org.coepi.android.ble.covidwatch

import android.bluetooth.BluetoothAdapter
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import org.coepi.android.ble.covidwatch.utils.toUUID
import org.coepi.android.cen.Cen
import org.coepi.android.system.log.LogTag.BLE_S
import org.coepi.android.system.log.log
import java.lang.Exception
import java.util.UUID
import java.util.concurrent.TimeUnit

class BLEScanner(val context: Context, adapter: BluetoothAdapter, private var supportHardwareBatchBLE:Boolean ) {

    /************************************************************************/
    // CoEpi modification
    // Register a callback, since we don't access DB here

    var callback: ((Cen) -> Unit)? = null
    /************************************************************************/

    companion object {
        private const val TAG = "BluetoothLeScanner"
    }

    private val scanner: BluetoothLeScannerCompat? = BluetoothLeScannerCompat.getScanner()

    var isScanning: Boolean = false

    private var handler = Handler()


    private var scanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.i(TAG, "onScanFailed errorCode=$errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            //if( result!= null ){
                processScanResult(result)
            //}
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            if( results.size > maxResultsGot ){
                maxResultsGot = results.size;
            }
            Log.i(TAG, "onBatchScanResults results=$results")
            results?.forEach { processScanResult(it) }
        }

        private fun processScanResult(result: ScanResult) {
            val scanRecord = result.scanRecord ?: return

            val contactEventIdentifier =
                scanRecord?.serviceData?.get(ParcelUuid(BluetoothService.CONTACT_EVENT_SERVICE))?.toUUID()

            if (contactEventIdentifier == null) {
                Log.i(
                    TAG,
                    "Scan result device.address=${result.device.address} RSSI=${result.rssi} CEN=N/A"
                )
            } else {
                Log.i(
                    TAG,
                    "Scan result device.address=${result.device.address} RSSI=${result.rssi} CEN=${contactEventIdentifier.toString()
                        .toUpperCase()}"
                )

//            val serviceUuid = this@BLEScannerImpl.serviceUuid ?: error("Didn't set service UUID")
//
//            val contactEventIdentifier =
//                scanRecord.serviceData[ParcelUuid(serviceUuid)]?.toUUID()
//
//            if (contactEventIdentifier == null) {
//                log.i("Scan result device.address=${result.device.address} RSSI=${result.rssi} CEI=N/A", BLE_S)
//                // TODO: Handle case when CEI cannot be extracted from scan record.
//            } else {
//                log.i("Scan result device.address=${result.device.address} RSSI=${result.rssi} " +
//                        "CEI=${contactEventIdentifier.toString().toUpperCase()}", BLE_S)
//
//                scanRecord.serviceUuids.filter { it.uuid == serviceUuid }.forEach { uuid ->
//                    scanRecord.serviceData[uuid]?.let { bytes ->
//                        callback?.invoke(Cen(bytes))
//                    }
//                }
//            }

                /************************************************************************/
                // CoEpi modification
                // Pass the CEN found in the advertisement data to the callback

                scanRecord.serviceUuids?.filter { it.uuid == BluetoothService.CONTACT_EVENT_SERVICE }?.forEach { uuid ->
                    if( scanRecord.serviceData != null ) {
                        var uuiddata = scanRecord.serviceData
                        if( uuiddata != null ) {
                            uuiddata[uuid]?.let { bytes ->
                                callback?.invoke(Cen(bytes))
                            }
                        }
                    }
                }
                // Original code
//                CovidWatchDatabase.databaseWriteExecutor.execute {
//                    val dao: ContactEventDAO =
//                        CovidWatchDatabase.getInstance(context).contactEventDAO()
//                    val contactEvent = ContactEvent(contactEventIdentifier.toString())
//                    val isCurrentUserSick = context.getSharedPreferences(
//                        context.getString(R.string.preference_file_key),
//                        Context.MODE_PRIVATE
//                    ).getBoolean(
//                        context.getString(R.string.preference_is_current_user_sick),
//                        false
//                    )
//                    contactEvent.wasPotentiallyInfectious = isCurrentUserSick
//                    dao.insert(contactEvent)
//                }
                /************************************************************************/
            }
        }
    }

    var numHardwareAttemp = 0;
    var maxResultsGot = 0;
    var useHardwareBatchBLE = supportHardwareBatchBLE;

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun startScanning(serviceUUIDs: Array<UUID>?) {
        if (isScanning) return

        try {
//            val scanFilters = serviceUUIDs?.map {
//                ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build()
//            }

            val scanFilters: MutableList<ScanFilter> = ArrayList()

            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT)/**/
                .setReportDelay(1000)//this uses onBatchScanResults instead of onScanResult
                .setLegacy(false)
                .setUseHardwareBatchingIfSupported(useHardwareBatchBLE)
            .build()
            isScanning = true
            scanner?.startScan(scanFilters, scanSettings, scanCallback)

            Log.i(TAG, "Started scan")
        } catch (exception: Exception) {
            Log.e(TAG, "Start scan failed: $exception")
        }

        // Bug workaround: Restart periodically so the Bluetooth daemon won't get into a borked state
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (isScanning) {
                if( maxResultsGot == 0 && supportHardwareBatchBLE){
                    numHardwareAttemp++
                    if( numHardwareAttemp % 6 == 0 ){
                        //after 6 attempts (1min) to use hardware I've found no result
                        // try avoid use hardware, it consumes more battery, but it may work!
                        //switch between hardware and software
                        useHardwareBatchBLE = !useHardwareBatchBLE
                    }
                }
                Log.i(TAG, "Restarting scan...")
                stopScanning()
                startScanning(serviceUUIDs)
            }
        }, TimeUnit.SECONDS.toMillis(10))
    }

    fun stopScanning() {
        if (!isScanning) return

        try {
            isScanning = false
            scanner?.stopScan(scanCallback)
            Log.i(TAG, "Stopped scan")
        } catch (exception: Exception) {
            Log.e(TAG, "Stop scan failed: $exception")
        }
    }

}
