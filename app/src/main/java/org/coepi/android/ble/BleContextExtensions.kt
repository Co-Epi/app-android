package org.coepi.android.ble

import android.bluetooth.BluetoothManager
import android.content.Context
import org.coepi.android.system.log

val Context.bluetoothManager get(): BluetoothManager? =
    getSystemService(Context.BLUETOOTH_SERVICE).also {
        if (it == null) {
            log.e("Couldn't get bluetooth service")
        }
    }.let { service ->
        (service as? BluetoothManager).also { manager ->
            if (manager == null) {
                log.e("Service: $service hasn't expected class.")
            }
        }
    }
