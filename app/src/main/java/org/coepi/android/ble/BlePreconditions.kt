package org.coepi.android.ble

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.system.log.log
import org.coepi.android.ui.onboarding.OnboardingPermissionsChecker

class BlePreconditions(
    private val startPermissionsChecker: OnboardingPermissionsChecker,
    private val blePreconditionsNotifier: BlePreconditionsNotifier,
    private val bleEnabler: BleEnabler
) {
    private val disposables = CompositeDisposable()

    fun onActivityCreated(activity: Activity) {
        observeResults()

        showEnableBleAfterPermissions(activity)
        startPermissionsChecker.checkForPermissions(activity)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        bleEnabler.onActivityResult(requestCode, resultCode, data)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                   grantResults: IntArray) {
        startPermissionsChecker.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("CheckResult")
    private fun showEnableBleAfterPermissions(activity: Activity) {
        startPermissionsChecker.observable.subscribe {
            bleEnabler.enable(activity)
        }
    }

    private fun observeResults() {
        disposables += combineLatest(startPermissionsChecker.observable, bleEnabler.observable)
            .subscribeBy { (permissionsGranted, bleEnabled) ->
                if (permissionsGranted && bleEnabled) {
                    blePreconditionsNotifier.notifyBleEnabled()
                } else {
                    log.i("BLE preconditions not met. Permissions: $permissionsGranted, ble enabled: $bleEnabled")
                    // TODO handle?
                }
            }
    }
}
