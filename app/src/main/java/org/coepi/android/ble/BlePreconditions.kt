package org.coepi.android.ble

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.content.Intent
import android.util.Log
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.coepi.android.TAG_BLE_LOG
import org.coepi.android.system.log.log

class BlePreconditions(private val activity: Activity, onReady: () -> Unit) {
    private val btEnabler = BleEnabler(activity) { enabled ->
        Log.i(TAG_BLE_LOG,"EnablingBLE");
        if (enabled) {
            onReady()
        } else {
            val err= "Couldn't enable bluetooth";
            log.e(err)
            Log.i(TAG_BLE_LOG,err);
        }
    }

    fun onActivityCreated() {
        Log.i(TAG_BLE_LOG,"ACCESS_COARSE_LOCATION");
        activity.runWithPermissions(ACCESS_COARSE_LOCATION) {
            btEnabler.enable()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        btEnabler.onActivityResult(requestCode, resultCode, data)
    }
}
