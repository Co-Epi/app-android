package org.coepi.android.ble.covidwatch

import android.bluetooth.BluetoothAdapter
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
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import org.coepi.android.cen.Cen
import org.coepi.android.extensions.toHex
import org.coepi.android.system.log.LogTag.BLE_A
import org.coepi.android.system.log.log
import java.util.UUID

class BLEAdvertiser(val context: Context, adapter: BluetoothAdapter) {

    /************************************************************************/
    // CoEpi modification
    var writeCallback: ((Cen) -> Unit)? = null
    /************************************************************************/

    companion object {
        private const val TAG = "BluetoothLeAdvertiser"
    }

    private val advertiser: BluetoothLeAdvertiser? = adapter.bluetoothLeAdvertiser

    private var bluetoothGattServer: BluetoothGattServer? = null

    /************************************************************************/
    // CoEpi modification
    // Use Cen
    private var advertisedContactEventIdentifier: Cen? = null
    // Original code
//    private var advertisedContactEventIdentifier: UUID? = null
    /************************************************************************/

    private val advertiseCallback: AdvertiseCallback = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.w(TAG, "onStartSuccess settingsInEffect=$settingsInEffect")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Log.e(TAG, "onStartFailure errorCode=$errorCode")
        }
    }

    private var bluetoothGattServerCallback: BluetoothGattServerCallback =
        object : BluetoothGattServerCallback() {

            override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
                super.onServiceAdded(status, service)
                Log.i(TAG, "onServiceAdded status=$status service=$service")
            }

            override fun onCharacteristicReadRequest(
                device: BluetoothDevice?,
                requestId: Int,
                offset: Int,
                characteristic: BluetoothGattCharacteristic?
            ) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic)

                var result = BluetoothGatt.GATT_SUCCESS
                var value: ByteArray? = null

                try {
                    if (characteristic?.uuid == BluetoothService.CONTACT_EVENT_IDENTIFIER_CHARACTERISTIC) {
                        if (offset != 0) {
                            result = BluetoothGatt.GATT_INVALID_OFFSET
                            return
                        }

                        /************************************************************************/
                        // CoEpi modification
                        // Use CEN
                        // Send CEN in read request iOS(c) - Android(p)
                        // NOTE: we use the stored advertisedContactEventIdentifier (set in startAdvertising)
                        // startAdvertising is called periodically with a new CEN by CenRepo
                        // Not generating a new one on each request (differently to the iOS impl)
                        // clarify whether we need to generate one here.
                        // Note also that covidwatch stores advertisedContactEventIdentifier in variable but doesn't do anything with it.

                        value = advertisedContactEventIdentifier?.bytes

                        // Original code
//                        val newContactEventIdentifier = UUID.randomUUID()
//                        logContactEventIdentifier(newContactEventIdentifier)
//                        value = newContactEventIdentifier.toBytes()
                        /************************************************************************/


                    } else {
                        result = BluetoothGatt.GATT_FAILURE
                    }
                } catch (exception: Exception) {
                    result = BluetoothGatt.GATT_FAILURE
                    value = null
                } finally {
                    Log.i(
                        TAG,
                        "onCharacteristicReadRequest result=$result device=$device requestId=$requestId offset=$offset characteristic=$characteristic"
                    )
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        result,
                        offset,
                        value
                    )
                }
            }

            override fun onCharacteristicWriteRequest(
                device: BluetoothDevice?,
                requestId: Int,
                characteristic: BluetoothGattCharacteristic?,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray?
            ) {
                super.onCharacteristicWriteRequest(
                    device,
                    requestId,
                    characteristic,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value
                )

                var result = BluetoothGatt.GATT_SUCCESS
                try {
                    if (characteristic?.uuid == BluetoothService.CONTACT_EVENT_IDENTIFIER_CHARACTERISTIC) {
                        if (offset != 0) {
                            result = BluetoothGatt.GATT_INVALID_OFFSET
                            return
                        }

                        /************************************************************************/
                        // CoEpi modification
                        // Advetiser was written to

                        if (value == null) {
                            result = BluetoothGatt.GATT_FAILURE
                            return
                        }
                        log.i("CEN was written to my advertiser: ${value.toHex()}", BLE_A)
                        writeCallback?.invoke(Cen(value))

                        // Original code
//                        val newContactEventIdentifier = value?.toUUID()
//                        if (newContactEventIdentifier == null) {
//                            result = BluetoothGatt.GATT_FAILURE
//                            return
//                        }
//
//                        logContactEventIdentifier(newContactEventIdentifier)
                        /************************************************************************/

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
        }

    fun startAdvertising(
        serviceUUID: UUID?,
        /************************************************************************/
        // CoEpi modification
        // use CEN
        contactEventIdentifier: Cen?
        // Original code
//        contactEventIdentifier: UUID?
        /************************************************************************/
    ) {
        try {
            advertisedContactEventIdentifier = contactEventIdentifier

            val advertiseSettings = AdvertiseSettings.Builder()
                // Use low latency mode so the chance of being discovered is higher
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                // Use low power so the discoverability range is short
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                // Use true so devices can connect to our GATT server
                .setConnectable(true)
                // Advertise forever
                .setTimeout(0)
                .build()

            val advertiseData = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceUuid(ParcelUuid(serviceUUID))
                /************************************************************************/
                // CoEpi modification
                // Different syntax to retrieve bytes
                .addServiceData(ParcelUuid(serviceUUID), contactEventIdentifier?.bytes)
                // Original code
//                .addServiceData(ParcelUuid(serviceUUID), contactEventIdentifier?.toBytes())
                /************************************************************************/
//                .addServiceData(ParcelUuid(serviceUUID), ByteArray(20))
                .build()

            (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager).let { bluetoothManager ->

                bluetoothGattServer =
                    bluetoothManager?.openGattServer(context, bluetoothGattServerCallback)

                val service = BluetoothGattService(
                    BluetoothService.CONTACT_EVENT_SERVICE,
                    BluetoothGattService.SERVICE_TYPE_PRIMARY
                )
                service.addCharacteristic(
                    BluetoothGattCharacteristic(
                        BluetoothService.CONTACT_EVENT_IDENTIFIER_CHARACTERISTIC,
                        BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
                        BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
                    )
                )

                bluetoothGattServer?.clearServices()
                bluetoothGattServer?.addService(service)
            }

            advertiser?.startAdvertising(advertiseSettings, advertiseData, advertiseCallback)

            Log.i(TAG, "Started advertising")
        } catch (exception: java.lang.Exception) {
            Log.e(TAG, "Start advertising failed: $exception")
        }
    }

    fun stopAdvertising() {
        try {
            advertiser?.stopAdvertising(advertiseCallback)
            bluetoothGattServer?.apply {
                clearServices()
                close()
            }
            bluetoothGattServer = null

            Log.i(TAG, "Stopped advertising")
        } catch (exception: java.lang.Exception) {
            Log.e(TAG, "Stop advertising failed: $exception")
        }
    }

    /**
     * Changes the CEN to a new random UUID in the service data field
     * NOTE: This will also log the CEN and stop/start the advertiser
     */
    fun changeContactEventIdentifierInServiceDataField(newContactEventIdentifier: Cen) {
        Log.i(TAG, "Changing the contact event identifier in service data field...")
        stopAdvertising()
        /************************************************************************/
        // CoEpi modification
        // We don't need this as storage not managed here
//        val newContactEventIdentifier = UUID.randomUUID()
//        logContactEventIdentifier(newContactEventIdentifier)
        /************************************************************************/
        startAdvertising(BluetoothService.CONTACT_EVENT_SERVICE, newContactEventIdentifier)
    }

    /************************************************************************/
    // CoEpi modification
    // We don't need this as storage not managed here
    // Original code
//    fun logContactEventIdentifier(identifier: UUID) {
//        CovidWatchDatabase.databaseWriteExecutor.execute {
//            val dao: ContactEventDAO = CovidWatchDatabase.getInstance(context).contactEventDAO()
//            val contactEvent = ContactEvent(identifier.toString())
//            val isCurrentUserSick = context.getSharedPreferences(
//                context.getString(R.string.preference_file_key),
//                Context.MODE_PRIVATE
//            ).getBoolean(context.getString(R.string.preference_is_current_user_sick), false)
//            contactEvent.wasPotentiallyInfectious = isCurrentUserSick
//            dao.insert(contactEvent)
//        }
//    }
    /************************************************************************/
}
