package org.coepi.android.cross

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.log.log
import org.coepi.android.ui.debug.DebugBleObservable

class ScannedCensHandler(
    bleManager: BleManager,
    private val coEpiRepo: CoEpiRepo,
    private val debugBleObservable: DebugBleObservable
) {
    private val disposables = CompositeDisposable()

    init {
        disposables += bleManager.observedCens
            .subscribeBy(onNext = { cen ->
                log.d("Storing an observed CEN: $cen")
                coEpiRepo.storeObservedCen(ReceivedCen(cen, now()))
                debugBleObservable.setObservedCen(cen)
            }, onError = {
                log.e("Error scanning: $it")
            })
    }
}
