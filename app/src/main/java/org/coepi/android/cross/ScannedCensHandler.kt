package org.coepi.android.cross

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.domain.CoEpiDate.Companion.now
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.log.log

class ScannedCensHandler(
    bleManager: BleManager,
    private val coEpiRepo: CoEpiRepo
) {
    private val disposables = CompositeDisposable()

    init {
        disposables += bleManager.observedCens
            .subscribeBy(onNext = { cen ->
                coEpiRepo.storeObservedCen(ReceivedCen(cen, now()))
            }, onError = {
                log.e("Error scanning: $it")
            })
    }
}
