package org.coepi.android.cross

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.ble.BleManager
import org.coepi.android.system.log.log
import org.coepi.android.ui.debug.DebugBleObservable
import org.coepi.core.services.ObservedTcnsRecorder

class ScannedTcnsHandler(
    bleManager: BleManager,
    private val debugBleObservable: DebugBleObservable,
    private val observedTcnsRecorder: ObservedTcnsRecorder
) {
    private val disposables = CompositeDisposable()

    init {
        disposables += bleManager.observedTcns
            .subscribeBy(onNext = { tcn ->
                log.d("Observed TCN: $tcn")
                observedTcnsRecorder.recordTcn(tcn).also {
                    log.v("Inserted observed TCN: $tcn")
                }
                debugBleObservable.setObservedTcn(tcn)
            }, onError = {
                log.e("Error scanning: $it")
            })
    }
}
