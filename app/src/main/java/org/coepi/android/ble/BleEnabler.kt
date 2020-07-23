package org.coepi.android.ble

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE
import android.content.Intent
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.MainActivity.RequestCodes
import org.coepi.android.system.log.LogTag.PERM
import org.coepi.android.system.log.log

class BleEnabler {
    private val requestCode = RequestCodes.enableBluetooth

    val observable: PublishSubject<Boolean> = create()

    fun enable(activity: Activity) {
        val adapter = activity.bluetoothManager?.adapter

        if (adapter != null) {
            if (adapter.isEnabled) {
                log.d("Bluetooth is enabled", PERM)
                observable.onNext(true)
            } else {
                log.d("Bluetooth not enabled. Requesting...", PERM)
                val enableBluetoothIntent = Intent(ACTION_REQUEST_ENABLE)
                activity.startActivityForResult(enableBluetoothIntent, requestCode)
            }
        } else {
            // No BT adapter
            observable.onNext(false)
        }
    }

    /**
     * Update state if for reasons extraneous to this class, BT will not be enabled.
     * Currently when required permissions are not granted.
     */
    fun notifyWillNotBeEnabled() {
        observable.onNext(false)
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
