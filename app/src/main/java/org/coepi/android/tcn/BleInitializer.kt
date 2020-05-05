package org.coepi.android.tcn

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.ble.BlePreconditionsNotifier
import org.coepi.android.system.log.log

class BleInitializer(
    private val blePreconditions: BlePreconditionsNotifier,
    private val bleManager: BleManager
) {
    private val disposables = CompositeDisposable()

    fun start() {
        startBleWhenEnabled()
    }

    private fun startBleWhenEnabled() {
        disposables += blePreconditions.bleEnabled
        .take(1)
        .subscribeBy(onNext = {
            log.i("BlePreconditions met - starting BLE")
            bleManager.startService()
        }, onError = {
            log.i("Error enabling bluetooth: $it")
        })
    }
}
