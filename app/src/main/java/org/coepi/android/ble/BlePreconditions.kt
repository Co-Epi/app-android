package org.coepi.android.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.system.log.LogTag.PERM
import org.coepi.android.system.log.log
import org.coepi.android.ui.onboarding.GrantedPermissions
import org.coepi.android.ui.onboarding.GrantedPermissions.ALL
import org.coepi.android.ui.onboarding.GrantedPermissions.NONE
import org.coepi.android.ui.onboarding.GrantedPermissions.ONLY_FOREGROUND
import org.coepi.android.ui.onboarding.OnboardingPermissionsChecker

class BlePreconditions(
    private val startPermissionsChecker: OnboardingPermissionsChecker,
    private val blePreconditionsNotifier: BlePreconditionsNotifier,
    private val bleEnabler: BleEnabler
) {
    private val disposables = CompositeDisposable()

    fun onActivityCreated(activity: Activity) {
        observeBleEnabled()
        showEnableBleAfterPermissions(activity)
        startPermissionsChecker.requestPermissionsIfNeeded(activity)

    }

    fun onActivityDestroy(activity: Activity) {
        disposables.clear()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        bleEnabler.onActivityResult(requestCode, resultCode, data)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                   grantResults: IntArray, activity: Activity) {
        startPermissionsChecker.onRequestPermissionsResult(requestCode, permissions, grantResults,
            activity)
    }

    private fun showEnableBleAfterPermissions(activity: Activity) {
        disposables += startPermissionsChecker.observable.subscribe { permissions: GrantedPermissions ->
            log.i("Handling permissions result: $permissions", PERM)
            when (permissions) {
                ALL, ONLY_FOREGROUND -> bleEnabler.enable(activity)
                NONE -> bleEnabler.notifyWillNotBeEnabled()
            }
        }
    }

    private fun observeBleEnabled() {
        disposables += bleEnabler.observable.subscribeBy { bleEnabled ->
            if (bleEnabled) {
                blePreconditionsNotifier.notifyBleEnabled()
            } else {
                log.i("User didn't enable BLE", PERM)
                // TODO UX
            }
        }
    }
}
