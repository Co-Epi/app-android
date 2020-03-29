package org.coepi.android.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData.Builder
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY
import android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_HIGH
import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetCallback
import android.bluetooth.le.AdvertisingSetParameters
import android.bluetooth.le.AdvertisingSetParameters.INTERVAL_HIGH
import android.bluetooth.le.AdvertisingSetParameters.TX_POWER_MEDIUM
import android.os.ParcelUuid
import androidx.lifecycle.ViewModel
import org.coepi.android.cen.CenRepo
import org.coepi.android.system.log.log
import java.util.UUID

class BleAdvertiser(private val adapter: BluetoothAdapter, private val repo: CenRepo) : ViewModel() {

    /* requires minSDK=26
      * 2020-03-29 04:19:39.417 7774-7774/org.coepi.android E/AndroidRuntime: FATAL EXCEPTION: main
        Process: org.coepi.android, PID: 7774
        bjava.lang.NoClassDefFoundError: Failed resolution of: Landroid/bluetooth/le/AdvertisingSetParameters$Builder;

        BleAdvertiser(private val adapter: BluetoothAdapter, private val repo: CenRepo) : ViewModel() {
        val parameters = AdvertisingSetParameters.Builder()
         requires minSDK= 26

         tried this older example: https://code.tutsplus.com/tutorials/how-to-advertise-android-as-a-bluetooth-le-peripheral--cms-25426

    val params = AdvertisingSetParameters.Builder()
        .setLegacyMode(true)
        .setConnectable(true)
        .setScannable(true)
        .setInterval(INTERVAL_HIGH)
        .setTxPowerLevel(TX_POWER_MEDIUM)
        .build()*/

    val settings = AdvertiseSettings.Builder()
        .setAdvertiseMode(ADVERTISE_MODE_LOW_LATENCY)
        .setTxPowerLevel(ADVERTISE_TX_POWER_HIGH)
        .setConnectable(true)
        .build()

    var started = false

    fun startAdvertising(serviceUuid: UUID) {
        if (!adapter.enableIfNotEnabled()) {
            log.e("Couldn't enable bluetooth. Can't advertise.")
        }
        val advertiser = adapter.bluetoothLeAdvertiser ?: run {
            log.e("No advertiser. Can't advertise.")
            return
        }
        android.util.Log.i("BleAdvertiser", "startAdvertising")

        val data = Builder()
            .addServiceUuid(ParcelUuid(serviceUuid))
            .build()

        val advertisingCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                log.i("Advertising onStartSuccess. Settings: $settingsInEffect")
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                log.e("Advertising onStartFailure. Error: $errorCode")
                super.onStartFailure(errorCode)
            }
        }
        repo.CEN.observeForever { cen ->
            // ServiceData holds Android Contact Event Number (CEN) that the Android peripheral is advertising
            val cenString = cen.toString()
            log.i("BleAdvertiser - observeForever CEN: $cenString")
            if ( started ) {
                advertiser.stopAdvertising(advertisingCallback)
            }
            if ( cen != null ) {
                Builder().addServiceUuid(ParcelUuid(serviceUuid))
                    .addServiceData(ParcelUuid(serviceUuid), cen)
                    .build()
                advertiser.run {
                    started = true
                    startAdvertising(settings, data, advertisingCallback)
                }
            }
        }

        advertiser.run {
            val data = Builder().setIncludeDeviceName(true).build()
            startAdvertising(settings, data, advertisingCallback )
        }
        log.i("BleAdvertiser - Started advertising")
    }

    private fun BluetoothAdapter.enableIfNotEnabled(): Boolean =
        if (!isEnabled) {
            enable()
        } else {
            true
        }


    /* requires minsdk=26
    private val advertisingCallbackParams = object : AdvertisingSetCallback() {
        override fun onAdvertisingSetStarted(advertisingSet: AdvertisingSet?, txPower: Int, status: Int) {
            log.i("onAdvertisingSetStarted(): txPower: $txPower, status: $status, advertisingSet: $advertisingSet")
            advertisingSet?.enableAdvertising() ?: {
                log.e("Advertising set is not set. Can't enable advertising.")
            }()
        }

        override fun onAdvertisingDataSet(advertisingSet: AdvertisingSet, status: Int) {
            log.i("onAdvertisingDataSet(): status: $status")
        }

        override fun onScanResponseDataSet(advertisingSet: AdvertisingSet, status: Int) {
            log.i("onScanResponseDataSet(): status: $status")
            log.d("Current scan mode: ${adapter.scanMode}")
        }

        override fun onAdvertisingSetStopped(advertisingSet: AdvertisingSet) {
            log.i("onAdvertisingSetStopped(): $advertisingSet")
        }
    }*/


    private fun AdvertisingSet.enableAdvertising() {
        /*
        requires minSDK=26
         */
        setAdvertisingData(Builder()
            .setIncludeDeviceName(true)
            .setIncludeTxPowerLevel(true)
            .build())
        setScanResponseData(Builder()
            .addServiceUuid(ParcelUuid(UUID.randomUUID()))
            .build())
        enableAdvertising(true, 65535 /* max */, 255)
    }
}
