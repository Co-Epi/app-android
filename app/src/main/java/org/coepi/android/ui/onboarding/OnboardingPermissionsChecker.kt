package org.coepi.android.ui.onboarding

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.MainActivity.RequestCodes
import org.coepi.android.R
import org.coepi.android.system.log.log

class OnboardingPermissionsChecker {

    val observable: PublishSubject<Boolean> = create()

    private val requestCode = RequestCodes.onboardingPermissions

    fun checkForPermissions(activity: Activity) {
        var permissions = arrayOf(BLUETOOTH, ACCESS_COARSE_LOCATION)
        if (SDK_INT >= Q) {
            permissions += listOf(ACCESS_BACKGROUND_LOCATION)
        }
        val hasAllPermissions = permissions.all {
            checkSelfPermission(activity, it) == PERMISSION_GRANTED
        }
        when {
            hasAllPermissions -> observable.onNext(true)
            shouldShowRequestPermissionRationale(activity, BLUETOOTH) ->
                AlertDialog.Builder(activity)
                    .setTitle(R.string.bluetooth_info_title)
                    .setMessage(R.string.bluetooth_info_message)
                    .setPositiveButton(R.string.ok, DialogInterface.OnClickListener
                    { _, _ ->
                        requestPermissions(activity, permissions, requestCode)
                    })
                    .setNegativeButton(R.string.dont_allow, DialogInterface.OnClickListener
                    { dialog, _ ->
                        dialog.dismiss()
                    })
                    .show()
            else -> requestPermissions(activity, permissions, requestCode)
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode != this.requestCode) return

        if (grantResults.all { it == PERMISSION_GRANTED }) {
            log.i("Permissions granted")
            observable.onNext(true)
        } else {
            log.i("Permissions denied")
            observable.onNext(false)
        }
    }
}
