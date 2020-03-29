package org.coepi.android.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import org.coepi.android.cen.CenRepo
import org.coepi.android.system.log.log

class BlePeripheral(context: Context, private val repo: CenRepo) {
    init {
        android.util.Log.i("BlePeripheral", "init")
        val manager = context.getSystemService(BLUETOOTH_SERVICE)
                as? BluetoothManager
        val adapter = manager?.adapter
        if (manager != null && adapter != null) {
            start(manager, adapter, context)
        } else {
            log.e("Couldn't initialize peripheral. Check that you're using a real device.")
        }
    }

    private fun start(manager: BluetoothManager, adapter: BluetoothAdapter, context: Context) {
        BleAdvertiser(adapter).startAdvertising(Uuids.service)
        BleServiceManager(manager, context)
        log.i("Started peripheral")

        android.util.Log.i("BlePeripheral", "start")
    }
}
