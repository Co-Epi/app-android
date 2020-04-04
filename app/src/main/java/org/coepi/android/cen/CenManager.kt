package org.coepi.android.cen

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.ble.BlePreconditionsNotifier
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.log.log

class CenManager(
    private val blePreconditions: BlePreconditionsNotifier,
    private val bleManager: BleManager,
    private val cenRepo: CenRepo,
    private val coepiRepo: CoEpiRepo
) {
    private val disposables = CompositeDisposable()

    fun start() {
        startBleWhenEnabled()
    }

    private fun startBleWhenEnabled() {
        disposables += Observables.combineLatest(
            blePreconditions.bleEnabled,
            // Take the first CEN, needed to start the service
            cenRepo.generatedCen
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
        observeScannedCens()
    }

    private fun forwardRepoCenToBleAdvertiser() {
        disposables += cenRepo.generatedCen.subscribeBy (onNext = { cen ->
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

    private fun observeScannedCens() {
        disposables += bleManager.scanObservable
            .subscribeBy(onNext = {
                coepiRepo.storeObservedCen(it)
            }, onError = {
                log.e("Error scanning: $it")
            })
    }
}
