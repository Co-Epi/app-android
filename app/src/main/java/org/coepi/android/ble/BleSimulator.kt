package org.coepi.android.ble

import io.reactivex.Observable
import org.coepi.android.extensions.hexToByteArray
import org.coepi.android.system.log.log
import org.coepi.core.domain.model.Tcn

class BleSimulator : BleManager {

    // Emits all the TCNs at once and terminates
    override val observedTcns: Observable<RecordedTcn> = Observable.fromIterable(listOf(
        RecordedTcn(Tcn("2485a64b57addcaea3ed1b538d07dbce".hexToByteArray()), 0.0)
    ))

    init {
        log.i("Using Bluetooth simulator")
    }

    override fun startService() {}
    override fun stopService() {}
}
