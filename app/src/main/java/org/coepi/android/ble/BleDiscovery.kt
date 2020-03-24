package org.coepi.android.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.coepi.android.cen.CENRepo
import org.coepi.android.system.log.log
import org.koin.core.KoinComponent
import org.koin.core.inject

interface BleDiscovery {
    val devices: Observable<BluetoothDevice>
}

class BleDiscoveryImpl(context: Context): BleDiscovery, KoinComponent {
    override val devices: BehaviorSubject<BluetoothDevice> = BehaviorSubject.create()
    val repo : CENRepo by inject()


    private val adapter: BluetoothAdapter? = context.bluetoothManager?.adapter.also {
        if (it == null) {
            log.d("Adapter is null")
        }
    }
    private val scanner: BluetoothLeScanner? = adapter?.bluetoothLeScanner

    fun discover() {
        android.util.Log.i("BleDiscoveryImpl", "Starting BLE discovery")
        scanner?.startScan(listOf(), ScanSettings.Builder().build(), callback)
    }

    fun stopDiscovery() {
        log.v("Stopping BLE discovery")
        scanner?.stopScan(callback)
    }

    private val callback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {

            result?.device?.let {
                result.scanRecord?.let {
                    val serviceUuids = it.serviceUuids
                    // check that the service UUID matches
                    if ( serviceUuids != null ) {
                        for (i in serviceUuids.indices) {
                            val s = serviceUuids[i]
                            val uuid = s.uuid.toString()
                            // check that we have a CoEpi UUID
                            if ( Uuids.service.toString() == s.uuid.toString() ) {
                                val serviceData = it.serviceData.toString()
                                // *************** The ServiceData IS WHERE WE TAKE THE ANDROID CEN that the Android peripheral is advertising and we record it in Contacts
                                log.i("Discovered CoEpi with ServiceData: $uuid $serviceData")
                                repo?.insertCEN(serviceData)
                            } else {
                                val x = Uuids.service.toString()
                                val serviceData = it.serviceData.toString()
                                log.d("Discovered non-CoEpi Service UUID: $x $serviceData")
                                repo?.let {
                                    it.insertCEN(serviceData)
                                }
                            }
                        }
                    }
                }
                devices.onNext(it)

            } ?: {
                log.v("Got scan result without a device")
            }()
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            log.d("Scan failed. Error code: $errorCode")
        }
    }
}
