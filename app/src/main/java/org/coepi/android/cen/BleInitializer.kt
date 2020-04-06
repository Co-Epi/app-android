package org.coepi.android.cen

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Observables.combineLatest
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.ble.BlePreconditionsNotifier
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.log.LogTag.BLE_S
import org.coepi.android.system.log.log

class BleInitializer(
    private val blePreconditions: BlePreconditionsNotifier,
    private val bleManager: BleManager,
    private val myCenProvider: MyCenProvider
) {
    private val disposables = CompositeDisposable()

    fun start() {
        startBleWhenEnabled()
    }

    private fun startBleWhenEnabled() {
        disposables += combineLatest(
            blePreconditions.bleEnabled,
            // Take the first CEN, needed to start the service
            myCenProvider.cen
        )
        .take(1)
        .subscribeBy(onNext = { (_, cen) ->
            log.i("BlePreconditions met - starting BLE")
            startBle(cen)
        }, onError = {
            log.i("Error enabling bluetooth: $it")
        })
    }

    private fun startBle(cen: Cen) {
        bleManager.startService(cen)
        forwardRepoCenToBleAdvertiser()
    }

    private fun forwardRepoCenToBleAdvertiser() {
        disposables += myCenProvider.cen.subscribeBy (onNext = { cen ->
            // ServiceData holds Android Contact Event Number (CEN) that the Android peripheral is advertising

            // TODO is check really needed? If yes, either add flag to advertiser or expose state and use here
//            if (started) {
            bleManager.stopAdvertiser()
//            }

            bleManager.startAdvertiser(cen)

        }, onError = {
            log.i("Error observing CEN: $it")
        })
    }
}
