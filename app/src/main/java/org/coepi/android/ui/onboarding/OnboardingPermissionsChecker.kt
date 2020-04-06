package org.coepi.android.ui.onboarding

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH
import android.Manifest.permission.BLUETOOTH_ADMIN
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.MainActivity.RequestCodes
import org.coepi.android.system.log.log


class OnboardingPermissionsChecker {

    val observable: PublishSubject<Boolean> = create()

    val GPS_ENABLE_REQUEST = 0x1001

    private val requestCode = RequestCodes.onboardingPermissions

    fun checkForPermissions(activity: Activity, context: Context) {
        var permissions = arrayOf(BLUETOOTH, /*BLUETOOTH_ADMIN,*/ ACCESS_COARSE_LOCATION/*, ACCESS_FINE_LOCATION*/)
        if (SDK_INT >= Q) {
            permissions += listOf(ACCESS_BACKGROUND_LOCATION)
        }
        requestPermissions(activity, permissions, requestCode)
        var hasAllPermissions = true;
        if (permissions.contains(BLUETOOTH) && ContextCompat.checkSelfPermission(activity, BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
            hasAllPermissions=false;
        }
        if (permissions.contains(BLUETOOTH_ADMIN) && ContextCompat.checkSelfPermission(activity, BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED){
            hasAllPermissions=false;
        }
        if (permissions.contains(ACCESS_COARSE_LOCATION) && ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            hasAllPermissions=false;
        }
        if (permissions.contains(ACCESS_FINE_LOCATION) && ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(ACCESS_FINE_LOCATION), 1);
            hasAllPermissions=false;
        }
        if (permissions.contains(ACCESS_BACKGROUND_LOCATION) && ContextCompat.checkSelfPermission(activity, ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(ACCESS_BACKGROUND_LOCATION), 1);
            hasAllPermissions=false;
        }

        if( ! hasAllPermissions ){
            hasAllPermissions = permissions.all {
                checkSelfPermission(activity, it) == PERMISSION_GRANTED
            }
        }
        if (hasAllPermissions) {
            //on moto C without gpsEnalbed
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var gpsEnabled = false
            try {
                gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
                Log.d("BLE_Permissions",ex.toString())
            }
            if( ! gpsEnabled ){
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(activity, intent, GPS_ENABLE_REQUEST, null )
            }
            observable.onNext(true)
        } else {
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                   grantResults: IntArray) {

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
