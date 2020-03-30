package org.coepi.android.ui.onboarding

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.MainActivity
import org.coepi.android.di.appModule
import org.coepi.android.extensions.toLiveData
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.SEEN_ONBOARDING
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class OnboardingViewModel(
    private val rootNav: RootNavigation,
    private val preferences: Preferences
) : ViewModel() {

    val context: Context = TODO("Need this to pick up main context, not sure how in VM");
    val activity: Activity = TODO("Need this to attach to main activity, not sure how in VM");
    val text: LiveData<String> = Observable.just("TODO onboarding").toLiveData()


    fun onCtaClick() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)) {
                val dialogBuilder = AlertDialog.Builder(context)
                val alert = dialogBuilder.create()
                alert.setTitle("Allow CoEpi access to Bluetooth?")
                dialogBuilder.setMessage("CoEpi uses Bluetooth to share interactions with other Bluetooth devices. Your data will by defaut be stored locally.")
                    .setPositiveButton("Allow", DialogInterface.OnClickListener {
                            _, _ -> this.closeOnboardingScreen()
                    })
                    .setNegativeButton("Don't Allow", DialogInterface.OnClickListener {
                            dialog, _ -> dialog.cancel()
                    })
                alert.show()
            }
        } else {
            this.closeOnboardingScreen()
        }
    }

    private fun closeOnboardingScreen() {
        preferences.putBoolean(SEEN_ONBOARDING, true);
        rootNav.navigate(Back)
    }
}
