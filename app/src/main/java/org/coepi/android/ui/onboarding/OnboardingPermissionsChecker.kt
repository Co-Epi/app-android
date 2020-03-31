package org.coepi.android.ui.onboarding

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import org.coepi.android.ble.BlePreconditions

class OnboardingPermissionsChecker(private val blePreconditions: BlePreconditions) {

    fun showPermissionCheck(onGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            val dialogBuilder = AlertDialog.Builder(context)
            val alert = dialogBuilder.create()
            alert.setTitle("Allow CoEpi access to Bluetooth?")
            dialogBuilder.setMessage("CoEpi uses Bluetooth to share interactions with other Bluetooth devices. Your data will by defaut be stored locally.")
                .setPositiveButton("Allow") { _, _ ->
                    run {
                        blePreconditions.onActivityCreated()
                        onGranted()
                    }
                }
                .setNegativeButton("Don't Allow") { dialog, _ -> dialog.cancel() }
            alert.show()
        }
        else {
            blePreconditions.onActivityCreated();
            onGranted();
        }
    }


}
