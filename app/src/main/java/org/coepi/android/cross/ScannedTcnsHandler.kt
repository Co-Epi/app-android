package org.coepi.android.cross

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.tcn.TcnDao
import org.coepi.android.tcn.ReceivedTcn
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.system.log.log
import org.coepi.android.ui.debug.DebugBleObservable

class ScannedTcnsHandler(
    bleManager: BleManager,
    private val debugBleObservable: DebugBleObservable,
    private val tcnDao: TcnDao
) {
    private val disposables = CompositeDisposable()

    init {
        disposables += bleManager.observedTcns
            .subscribeBy(onNext = { tcn ->
                log.d("Observed TCN: $tcn")
                if (tcnDao.insert(ReceivedTcn(tcn, now()))) {
                    log.v("Inserted observed TCN: $tcn")
                }
                debugBleObservable.setObservedTcn(tcn)
            }, onError = {
                log.e("Error scanning: $it")
            })
    }
}
