package org.tcncoalition.tcnclient.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.ParcelUuid
import android.util.Base64
import android.util.Log
import org.tcncoalition.tcnclient.TcnConstants
import java.util.Timer
import java.util.TimerTask
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TcnBluetoothManager(
    private val context: Context,
    private val scanner: BluetoothLeScanner,
    private val advertiser: BluetoothLeAdvertiser,
    private val tcnCallback: TcnBluetoothServiceCallback
) {

    private var bluetoothGattServer: BluetoothGattServer? = null

    private var isStarted: Boolean = false
    private var generatedTcn = ByteArray(0)
    private var tcnAdvertisingQueue = ArrayList<ByteArray>()
    private var inRangeBleAddressToTcnMap = mutableMapOf<String, ByteArray>()
    private var estimatedDistanceToRemoteDeviceAddressMap = mutableMapOf<String, Double>()

    private var handler = Handler()
    private var advertiseNextTcnTimer: Timer? = null

    private var executor: ExecutorService? = null

    fun start() {
        if (isStarted) return
        isStarted = true

        startScan()
        // Create the local GATTServer and open it once.
        initBleGattServer(
            (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager),
            TcnConstants.UUID_SERVICE
        )
        executor = Executors.newFixedThreadPool(2)
        changeOwnTcn() // This starts advertising also
        runAdvertiseNextTcnTimer()
    }

    fun stop() {
        if (!isStarted) return
        isStarted = false

        stopScan()
        stopAdvertising()
        bluetoothGattServer?.clearServices()
        bluetoothGattServer?.close()
        bluetoothGattServer = null

        handler.removeCallbacksAndMessages(null)
        advertiseNextTcnTimer?.cancel()
        advertiseNextTcnTimer = null

        tcnAdvertisingQueue.clear()
        inRangeBleAddressToTcnMap.clear()
        estimatedDistanceToRemoteDeviceAddressMap.clear()

        executor?.shutdown()
        executor = null
    }

    private fun runAdvertiseNextTcnTimer() {
        advertiseNextTcnTimer?.cancel()
        advertiseNextTcnTimer = Timer()
        advertiseNextTcnTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    if (tcnAdvertisingQueue.isEmpty()) return
                    val firstTCN = tcnAdvertisingQueue.first()
                    tcnAdvertisingQueue.removeAt(0)
                    tcnAdvertisingQueue.add(firstTCN)
                    stopAdvertising()
                    startAdvertising()
                }
            },
            TimeUnit.SECONDS.toMillis(20),
            TimeUnit.SECONDS.toMillis(20)
        )
    }

    fun changeOwnTcn() {
        executor?.execute {
            Log.i(TAG, "Changing own TCN ...")
            // Remove current TCN from the advertising queue.
            dequeueFromAdvertising(generatedTcn)
            val tcn = tcnCallback.generateTcn()
            Log.i(TAG, "Did generate TCN=${Base64.encodeToString(tcn, Base64.NO_WRAP)}")
            generatedTcn = tcn
            // Enqueue new TCN to the head of the advertising queue so it will be advertised next.
            enqueueForAdvertising(tcn, true)
            // Force restart advertising with new TCN
            stopAdvertising()
            startAdvertising()
        }
    }

    private fun dequeueFromAdvertising(tcn: ByteArray?) {
        tcn ?: return
        tcnAdvertisingQueue.remove(tcn)
        Log.i(TAG, "Dequeued TCN=${Base64.encodeToString(tcn, Base64.NO_WRAP)} from advertising")
    }

    private fun enqueueForAdvertising(tcn: ByteArray?, atHead: Boolean = false) {
        tcn ?: return
        if (atHead) {
            tcnAdvertisingQueue.add(0, tcn)
        } else {
            tcnAdvertisingQueue.add(tcn)
        }
        Log.i(TAG, "Enqueued TCN=${Base64.encodeToString(tcn, Base64.NO_WRAP)} for advertising")
    }

    private fun startScan() {
        if (!isStarted) return
        // Use try catch to handle DeadObject exception
        try {
            // The use of scan filters aren't required while the app is in the foreground.
            // This changes when the app is in the background. If they are missing then the Bluetooth
            // framework won't give us scan results.
            val scanFilters = arrayOf(TcnConstants.UUID_SERVICE).map {
                ScanFilter.Builder().setServiceUuid(ParcelUuid(it)).build()
            }

            val scanSettings = ScanSettings.Builder().apply {
                // Low latency is important for older Android devices to be able to discover nearby
                // devices.
                setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT)
                // Report delay plays an important role in keeping track of the devices nearby:
                // If a batch scan result doesn't include devices from the previous result,
                // then we consider those devices out of range.
                // Important: Using a large duration value (greater than 60 sec) won't get us scan
                // results on old OSes
                setReportDelay(TimeUnit.SECONDS.toMillis(5))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                    setLegacy(true)
                }
            }.build()

            scanner.startScan(scanFilters, scanSettings, scanCallback)
            Log.i(TAG, "Started scan")
        } catch (exception: Exception) {
            Log.e(TAG, "Start scan failed: $exception")
            startScan()
        }

        // Bug workaround: Restart periodically so the Bluetooth daemon won't get into a broken
        // state on old Android devices.
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            if (isStarted) {
                Log.i(TAG, "Restarting scan...")
                // If there are outstanding scan results then flush them so we can process them now
                // in onBatchScanResults
                scanner.flushPendingScanResults(scanCallback)
                stopScan()
                startScan()
            }
        }, TimeUnit.SECONDS.toMillis(20))
    }

    private fun stopScan() {
        // Use try catch to handle DeadObject exception
        try {
            scanner.stopScan(scanCallback)
            Log.i(TAG, "Stopped scan")
        } catch (exception: Exception) {
            Log.e(TAG, "Stop scan failed: $exception")
        }
    }

    private var scanCallback = object : ScanCallback() {

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "onScanFailed errorCode=$errorCode")
            if (errorCode == SCAN_FAILED_APPLICATION_REGISTRATION_FAILED) {
                startScan()
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)

            Log.d(TAG, "onBatchScanResults: ${results?.size}")

            // Search for a TCN in the service data of the advertisement
            results?.forEach for_each@{
                Log.d(TAG, "result=$it")

                val scanRecord = it.scanRecord ?: return@for_each

                val tcnServiceData = scanRecord.serviceData[
                    ParcelUuid(TcnConstants.UUID_SERVICE)]

                val hintIsAndroid = (tcnServiceData != null)

                // Update estimated distance
                val estimatedDistanceMeters = getEstimatedDistanceMeters(
                    it.rssi,
                    getMeasuredRSSIAtOneMeter(scanRecord.txPowerLevel, hintIsAndroid)
                )
                estimatedDistanceToRemoteDeviceAddressMap[it.device.address] =
                    estimatedDistanceMeters

                tcnServiceData ?: return@for_each
                if (tcnServiceData.size < TcnConstants.TEMPORARY_CONTACT_NUMBER_LENGTH) return@for_each
                val tcn =
                    tcnServiceData.sliceArray(0 until TcnConstants.TEMPORARY_CONTACT_NUMBER_LENGTH)

                Log.i(
                    TAG,
                    "Did find TCN=${Base64.encodeToString(
                        tcn,
                        Base64.NO_WRAP
                    )} from device=${it.device.address}\" at estimated distance=${estimatedDistanceToRemoteDeviceAddressMap[it.device.address]}"
                )
                tcnCallback.onTcnFound(
                    tcn,
                    estimatedDistanceToRemoteDeviceAddressMap[it.device.address]
                )
            }

            // Remove TCNs from our advertising queue that we received from devices which are now
            // out of range.
            var currentInRangeAddresses = results?.mapNotNull { it.device.address }
            if (currentInRangeAddresses == null) {
                currentInRangeAddresses = arrayListOf()
            }
            val addressesToRemove: MutableList<String> = mutableListOf()
            inRangeBleAddressToTcnMap.keys.forEach {
                if (!currentInRangeAddresses.contains(it)) {
                    addressesToRemove.add(it)
                }
            }
            addressesToRemove.forEach {
                val tcn = inRangeBleAddressToTcnMap[it]
                dequeueFromAdvertising(tcn)
                inRangeBleAddressToTcnMap.remove(it)
            }

            // Notify the API user that TCNs which are left in the list are still in range and
            // we have just found them again so it can track the duration of the contact.
            inRangeBleAddressToTcnMap.forEach {
                Log.i(
                    TAG,
                    "Did find TCN=${Base64.encodeToString(
                        it.value,
                        Base64.NO_WRAP
                    )} from device=${it.key} at estimated distance=${estimatedDistanceToRemoteDeviceAddressMap[it.key]}"
                )
                tcnCallback.onTcnFound(it.value, estimatedDistanceToRemoteDeviceAddressMap[it.key])
            }
        }
    }

    private fun startAdvertising() {
        if (!isStarted) return
        // Use try catch to handle DeadObject exception
        try {
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .setTimeout(0)
                .build()

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceUuid(ParcelUuid(TcnConstants.UUID_SERVICE))
                .addServiceData(
                    ParcelUuid(TcnConstants.UUID_SERVICE),
                    // Attach the first 4 bytes of our TCN to work around the problem of iOS
                    // devices writing a new TCN to us whenever we rotate the TCN (every 20 sec).
                    // iOS devices use the last 4 bytes to identify the Android devices and write
                    // only once a TCN to them.
                    tcnAdvertisingQueue.first() + generatedTcn.sliceArray(0..3)
                )
                .build()

            advertiser.startAdvertising(settings, data, advertisingCallback)
            Log.i(
                TAG, "Started advertising TCN=${Base64.encodeToString(
                    tcnAdvertisingQueue.first(),
                    Base64.NO_WRAP
                )} isOwn=${tcnAdvertisingQueue.first().contentEquals(generatedTcn)}"
            )
        } catch (exception: Exception) {
            Log.e(TAG, "Start advertising failed: $exception")
            startAdvertising()
        }
    }

    private fun initBleGattServer(
        bluetoothManager: BluetoothManager,
        serviceUUID: UUID?
    ) {
        bluetoothGattServer = bluetoothManager.openGattServer(context,
            object : BluetoothGattServerCallback() {
                override fun onCharacteristicWriteRequest(
                    device: BluetoothDevice?,
                    requestId: Int,
                    characteristic: BluetoothGattCharacteristic?,
                    preparedWrite: Boolean,
                    responseNeeded: Boolean,
                    offset: Int,
                    value: ByteArray?
                ) {
                    var result = BluetoothGatt.GATT_SUCCESS
                    try {
                        if (characteristic?.uuid == TcnConstants.UUID_CHARACTERISTIC) {
                            if (offset != 0) {
                                result = BluetoothGatt.GATT_INVALID_OFFSET
                                return
                            }

                            if (value == null || value.size != TcnConstants.TEMPORARY_CONTACT_NUMBER_LENGTH) {
                                result = BluetoothGatt.GATT_FAILURE
                                return
                            }

                            Log.i(
                                TAG,
                                "Did find TCN=${Base64.encodeToString(
                                    value,
                                    Base64.NO_WRAP
                                )} from device=${device?.address} at estimated distance=${estimatedDistanceToRemoteDeviceAddressMap[device?.address]}"
                            )
                            tcnCallback.onTcnFound(
                                value,
                                estimatedDistanceToRemoteDeviceAddressMap[device?.address]
                            )
                            // TCNs received through characteristic writes come from iOS apps in the
                            // background.
                            // We act as a bridge and advertise these TCNs so iOS apps can discover
                            // each other while in the background.
                            if (device != null) {
                                inRangeBleAddressToTcnMap[device.address] = value
                                enqueueForAdvertising(value)
                            }
                        } else {
                            result = BluetoothGatt.GATT_FAILURE
                        }
                    } catch (exception: Exception) {
                        result = BluetoothGatt.GATT_FAILURE
                    } finally {
                        Log.i(
                            TAG,
                            "onCharacteristicWriteRequest result=$result device=$device requestId=$requestId characteristic=$characteristic preparedWrite=$preparedWrite responseNeeded=$responseNeeded offset=$offset value=$value"
                        )
                        if (responseNeeded) {
                            bluetoothGattServer?.sendResponse(
                                device,
                                requestId,
                                result,
                                offset,
                                null
                            )
                        }
                    }
                }
            })

        val service = BluetoothGattService(
            serviceUUID,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )
        service.addCharacteristic(
            BluetoothGattCharacteristic(
                TcnConstants.UUID_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
            )
        )

        bluetoothGattServer?.clearServices()
        bluetoothGattServer?.addService(service)
    }

    private val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.w(TAG, "onStartSuccess settingsInEffect=$settingsInEffect")
            super.onStartSuccess(settingsInEffect)
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e(TAG, "onStartFailure errorCode=$errorCode")
            super.onStartFailure(errorCode)
        }
    }

    private fun stopAdvertising() {
        try {
            advertiser.stopAdvertising(advertisingCallback)
            Log.i(TAG, "Stopped advertising")
        } catch (exception: Exception) {
            Log.e(TAG, "Stop advertising failed: $exception")
        }
    }

    companion object {
        private const val TAG = "TcnBluetoothService"
    }
}
