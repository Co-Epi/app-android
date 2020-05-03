package org.coepi.android.cross

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.cen.CenDao
import org.coepi.android.cen.ReceivedCen
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.system.log.log
import org.coepi.android.ui.debug.DebugBleObservable

class ScannedCensHandler(
    bleManager: BleManager,
    private val debugBleObservable: DebugBleObservable,
    private val cenDao: CenDao
) {
    private val disposables = CompositeDisposable()

    init {
        disposables += bleManager.observedCens
            .subscribeBy(onNext = { cen ->
                log.d("Storing an observed CEN: $cen")
                if (cenDao.insert(ReceivedCen(cen, now()))) {
                    log.v("Inserted an observed CEN: $cen")
                }
                debugBleObservable.setObservedCen(cen)
            }, onError = {
                log.e("Error scanning: $it")
            })
    }
}
