package org.coepi.android.ble

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.app.Activity
import android.content.Intent
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.coepi.android.system.log

class BlePreconditions(private val activity: Activity, onReady: () -> Unit) {
    private val btEnabler = BleEnabler(activity) { enabled ->
        if (enabled) {
            onReady()
        } else {
            log.e("Couldn't enable bluetooth")
        }
    }

    fun onActivityCreated() {
        activity.runWithPermissions(ACCESS_COARSE_LOCATION) {
            btEnabler.enable()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        btEnabler.onActivityResult(requestCode, resultCode, data)
    }
}
