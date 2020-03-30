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
import java.util.UUID

interface BLEAdvertiser {
    fun startAdvertiser(serviceUUID: UUID?, characteristicUUID: UUID?, value: String?)
    fun stopAdvertiser()
    fun changeAdvertisedValue(value: String?)
    fun registerWriteCallback(callback: (String) -> Unit)
}

/**
 * BLEAdvertiser is responsible for advertising the bluetooth services.
 * Only one instance of this class is to be constructed, but its not enforced. (for now)
 * You have been warned!
 */
class BLEAdvertiserImpl(private val context: Context, adapter: BluetoothAdapter)
    : BLEAdvertiser {

    // BLE
    private val advertiser: BluetoothLeAdvertiser = adapter.bluetoothLeAdvertiser
    private var bluetoothGattServer: BluetoothGattServer? = null

    // TODO encapsulate these 2 maybe
    private var serviceUUID: UUID? = null
    private var characteristicUUID: UUID? = null

    private var advertisedValue: String? = null

    // Case Android (Central) - iOS (Peripheral)
    // https://docs.google.com/document/d/1f65V3PI214-uYfZLUZtm55kdVwoazIMqGJrxcYNI4eg/edit#
    private var writeCallback: ((String) -> Unit)? = null

    // CONSTANTS
    companion object {
        private const val TAG = "BLEAdvertiser"
    }

    /**
     * Callback when advertisements start and stops
     */
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

    private var bluetoothGattServerCallback: BluetoothGattServerCallback =
        object : BluetoothGattServerCallback() {

            override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
                super.onServiceAdded(status, service)
                Log.i(TAG, "onServiceAdded status=$status service=$service")
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
                    if (characteristic?.uuid == characteristicUUID) {
                        if (offset != 0) {
                            result = BluetoothGatt.GATT_INVALID_OFFSET
                            return
                        }

                        val str = value?.let { String(it) }
                        if (str == null) {
                            result = BluetoothGatt.GATT_FAILURE
                            return
                        }
                        writeCallback?.invoke(str)

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

    /**
     * Starts the advertiser, with the given UUID. We advertise with MEDIUM power to get
     * reasonable range, but this will need to be experimentally determined later.
     * ADVERTISE_MODE_LOW_LATENCY is a must as the other nodes are not real-time.
     *
     * @param serviceUUID The UUID to advertise the service
     * @param contactEventUUID The UUID that indicates the contact event
     */
    override fun startAdvertiser(
        serviceUUID: UUID?,
        characteristicUUID: UUID?,
        value: String?
    ) {
        this.serviceUUID = serviceUUID
        this.characteristicUUID = characteristicUUID
        advertisedValue = value

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setConnectable(true)
            .build()

        // TODO review this
        val advertisedValueBytes = advertisedValue?.toByteArray()

//        val testServiceDataMaxLength = ByteArray(20)
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceData(ParcelUuid(serviceUUID), advertisedValueBytes)
//            .addServiceData(ParcelUuid(serviceUUID), testServiceDataMaxLength)
            .build()

        (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).let { bluetoothManager ->

            bluetoothGattServer =
                bluetoothManager.openGattServer(context, bluetoothGattServerCallback)

            val service = BluetoothGattService(
                serviceUUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
            )
            service.addCharacteristic(
                BluetoothGattCharacteristic(
                    characteristicUUID,
                    BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
                )
            )

            bluetoothGattServer?.clearServices()
            bluetoothGattServer?.addService(service)
        }

        advertiser.startAdvertising(settings, data, advertisingCallback)
    }

    /**
     * Stops all BLE related activity
     */
    override fun stopAdvertiser() {
        advertiser.stopAdvertising(advertisingCallback)
        bluetoothGattServer?.clearServices()
        bluetoothGattServer?.close()
        bluetoothGattServer = null
    }

    /**
     * Changes the CEI to a new UUID in the service data field
     * NOTE: This will also stop/start the advertiser
     */
    override fun changeAdvertisedValue(value: String?) {
        Log.i(TAG, "Changing the contact event identifier in service data field...")
        stopAdvertiser()

        // TODO
//        logContactEventIdentifier(newContactEventIdentifier)

        startAdvertiser(serviceUUID, characteristicUUID, value)
    }

    override fun registerWriteCallback(callback: (String) -> Unit) {
        writeCallback = callback
    }

    fun logContactEventIdentifier(identifier: UUID) {
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
    }

}