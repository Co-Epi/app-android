package org.coepi.android.cen

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.ble.BlePreconditionsNotifier
import org.coepi.android.system.log.log

class CenManager(
    private val blePreconditions: BlePreconditionsNotifier,
    private val bleManager: BleManager,
    private val cenRepo: CenRepo
) {
    private val disposables = CompositeDisposable()

    fun start() {
        initServiceWhenBleIsEnabled()
        observeCen()
        observeScanner()
    }

    private fun initServiceWhenBleIsEnabled() {
        disposables += Observables.combineLatest(
            blePreconditions.bleEnabled,
            // Take the first CEN, needed to start the service
            cenRepo.cen
        )
        .take(1)
        .subscribeBy(onNext = { (_, firstCen) ->
            bleManager.startService(firstCen) // TODO review String <-> ByteArray
            log.i("BlePreconditions met - BLE manager started")
        }, onError = {
            log.i("Error enabling bluetooth: $it")
        })
    }

    /**
     * Sends CEN to advertiser when it's changed in DB
     */
    private fun observeCen() {
        disposables += cenRepo.cen.subscribeBy (onNext = { cen ->
            // ServiceData holds Android Contact Event Number (CEN) that the Android peripheral is advertising

            // TODO is check really needed? If yes, either add flag to advertiser or expose state and use here
//            if (started) {
            bleManager.stopAdvertiser()
//            }

            if (cen != null) {
                bleManager.startAdvertiser(cen)
            }
        }, onError = {
            log.i("Error observing CEN: $it")
        })
    }

    private fun observeScanner() {
        disposables += bleManager.scanObservable
            .subscribeBy(onNext = {
                cenRepo.insertCEN(it)
            }, onError = {
                log.e("Error scanning: $it")
            })
    }
}
