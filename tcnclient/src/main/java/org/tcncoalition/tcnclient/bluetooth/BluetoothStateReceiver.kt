package org.tcncoalition.tcnclient.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BluetoothStateReceiver(val bluetoothStateChanged: (bluetoothOn: Boolean) -> Unit) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.extras?.get(BluetoothAdapter.EXTRA_STATE)) {
            BluetoothAdapter.STATE_ON -> bluetoothStateChanged(true)
            BluetoothAdapter.STATE_OFF -> bluetoothStateChanged(false)
        }
    }
}