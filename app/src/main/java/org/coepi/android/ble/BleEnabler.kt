package org.coepi.android.ble

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE
import android.content.Intent
import io.reactivex.subjects.PublishSubject
import org.coepi.android.MainActivity
import org.coepi.android.MainActivity.RequestCodes
import org.coepi.android.system.log.log

class BleEnabler {
    private val requestCode = RequestCodes.enableBluetooth

    val observable: PublishSubject<Boolean> = PublishSubject.create()

    fun enable(activity: Activity) {
        val adapter = activity.bluetoothManager?.adapter

        if (adapter != null) {
            if (adapter.isEnabled) {
                observable.onNext(true)
            } else {
                log.d("Bluetooth not enabled. Requesting...")
                val enableBluetoothIntent = Intent(ACTION_REQUEST_ENABLE)
                activity.startActivityForResult(enableBluetoothIntent, requestCode)
            }
        } else {
            // No BT adapter
            observable.onNext(false)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == requestCode) {
            when (resultCode) {
                RESULT_OK -> observable.onNext(true)
                RESULT_CANCELED -> observable.onNext(false)
                else -> throw Exception("Unexpected result code: $resultCode")
            }
        }
    }
}
