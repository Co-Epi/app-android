package org.coepi.android.ble

import android.bluetooth.BluetoothDevice

val BluetoothDevice.debugDescription
    get() = "{Address: $address, name: $name, bt class: $bluetoothClass, " +
            "bond state: $bondState, type: $type, uuids: $uuids}"
