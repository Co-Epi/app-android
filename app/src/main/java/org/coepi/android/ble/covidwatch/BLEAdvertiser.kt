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
import org.coepi.android.system.log.LogTag.BLE_A
import org.coepi.android.system.log.log
import java.util.UUID

interface BLEAdvertiser {
    fun startAdvertiser(serviceUUID: UUID, characteristicUUID: UUID, cen: Cen)
    fun stopAdvertiser()
    fun changeAdvertisedValue(cen: Cen)
    fun registerWriteCallback(callback: (Cen) -> Unit)
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

    private var advertisedCen: Cen? = null

    // Case Android (Central) - iOS (Peripheral)
    // https://docs.google.com/document/d/1f65V3PI214-uYfZLUZtm55kdVwoazIMqGJrxcYNI4eg/edit#
    private var writeCallback: ((Cen) -> Unit)? = null

    /**
     * Callback when advertisements start and stops
     */
    private val advertisingCallback: AdvertiseCallback = object : AdvertiseCallback() {

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            log.d("onStartSuccess settingsInEffect=$settingsInEffect", BLE_A)
            super.onStartSuccess(settingsInEffect)
        }

        override fun onStartFailure(errorCode: Int) {
            log.e("onStartFailure errorCode=$errorCode", BLE_A)
            super.onStartFailure(errorCode)
        }
    }

    private var bluetoothGattServerCallback: BluetoothGattServerCallback =
        object : BluetoothGattServerCallback() {

            override fun onServiceAdded(status: Int, service: BluetoothGattService?) {
                super.onServiceAdded(status, service)
                log.i("onServiceAdded status=$status service=$service", BLE_A)
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

                        if (value == null) {
                            result = BluetoothGatt.GATT_FAILURE
                            return
                        }
                        writeCallback?.invoke(Cen(value))

                    } else {
                        result = BluetoothGatt.GATT_FAILURE
                    }
                } catch (exception: Exception) {
                    result = BluetoothGatt.GATT_FAILURE
                } finally {
                    log.i("onCharacteristicWriteRequest result=$result device=$device " +
                            "requestId=$requestId characteristic=$characteristic preparedWrite=$preparedWrite " +
                            "responseNeeded=$responseNeeded offset=$offset value=$value", BLE_A)

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
     * @param characteristicUUID The UUID that indicates the contact event
     */
    override fun startAdvertiser(
        serviceUUID: UUID,
        characteristicUUID: UUID,
        cen: Cen
    ) {
        this.serviceUUID = serviceUUID
        this.characteristicUUID = characteristicUUID
        advertisedCen = cen

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setConnectable(true)
            .build()

//        val testServiceDataMaxLength = ByteArray(20)
        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceData(ParcelUuid(serviceUUID), cen.bytes)
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
    override fun changeAdvertisedValue(cen: Cen) {
        val serviceUUID = serviceUUID ?: log.e("No service configured").run { return }
        val characteristicUUID =
            characteristicUUID ?: log.e("No characteristic configured").run { return }

        log.i("Changing the contact event identifier in service data field...", BLE_A)

        stopAdvertiser()
        startAdvertiser(serviceUUID, characteristicUUID, cen)
    }

    override fun registerWriteCallback(callback: (Cen) -> Unit) {
        writeCallback = callback
    }
}
