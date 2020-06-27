package org.coepi.android.ui.onboarding

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.MainActivity.RequestCodes
import org.coepi.android.R.string.bluetooth_info_message
import org.coepi.android.R.string.bluetooth_info_title
import org.coepi.android.R.string.dont_allow
import org.coepi.android.R.string.ok
import org.coepi.android.system.log.LogTag.PERM
import org.coepi.android.system.log.log

class OnboardingPermissionsChecker {
    val observable: PublishSubject<Boolean> = create()

    private val requestCode = RequestCodes.onboardingPermissions

    private val permissions: Array<String> = arrayOf(BLUETOOTH, ACCESS_COARSE_LOCATION) +
        if (SDK_INT >= Q) arrayOf(ACCESS_BACKGROUND_LOCATION) else emptyArray()

    fun requestPermissionsIfNeeded(activity: Activity): Unit {
        when {
            // User already granted permissions
            hasAllPermissions(activity) -> observable.onNext(true)

            // User denied without choosing "never ask again". This will always be true after
            // the first request, since it doesn't have "never ask again".
            shouldShowRationale(activity) -> showRationale(activity)

            // User has not been asked yet or denied checking "never ask again": let the system handle it
            // it will show a dialog or call directly onRequestPermissionsResult with the result.
            else -> requestPermissions(activity, permissions, requestCode)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                   grantResults: IntArray, activity: Activity) {
        if (requestCode != this.requestCode) return
        when {
            grantResults.all { it == PERMISSION_GRANTED } -> observable.onNext(true).also {
                log.i("Onboarding permissions granted", PERM)
            }
            grantResults.any { it == PERMISSION_DENIED } -> handlePermissionsDenied(activity)
            else -> log.e("Illegal state: permission result is not granted or denied.", PERM)
        }
    }

    private fun handlePermissionsDenied(activity: Activity) {
        if (shouldShowRationale(activity)) {
            log.d("Permissions were denied but we can ask again. Showing rationale.", PERM)
            showRationale(activity)
        } else {
            log.i("User denied required permissions and selected don't ask again.", PERM)
            // NOTE: UX not specified
            observable.onNext(false)
        }
    }

    private fun shouldShowRationale(activity: Activity): Boolean = permissions.any {
        shouldShowRequestPermissionRationale(activity, it)
    }

    private fun hasAllPermissions(activity: Activity): Boolean =  permissions.all {
        checkSelfPermission(activity, it) == PERMISSION_GRANTED
    }

    private fun showRationale(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle(bluetooth_info_title)
            .setMessage(bluetooth_info_message)
            .setPositiveButton(ok) { dialog, _ ->
                // After explaining, request again if they accept
                dialog.dismiss()
                requestPermissions(activity, permissions, requestCode)
            }
            .setNegativeButton(dont_allow) { dialog, _ ->
                // If the user doesn't accept the rationale, nothing happens.
                // The process starts again the next time they start the app.
                dialog.dismiss()
            }
            .show()
    }
}
