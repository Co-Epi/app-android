package org.tcncoalition.tcnclient.bluetooth

interface BluetoothStateListener {
    fun bluetoothStateChanged(bluetoothOn: Boolean)
}