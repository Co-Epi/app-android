package org.coepi.android.ui.debug

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.create
import org.coepi.android.ble.BleManager
import org.coepi.android.cen.Cen
import org.coepi.android.cen.CenKey

interface DebugBleObservable {
    val myKey: Observable<CenKey>
    val myCen: Observable<Cen>
    val observedCens: Observable<Cen>

    fun setMyKey(key: CenKey)
    fun setMyCen(cen: Cen)
    fun setObservedCen(cen: Cen)
}

class DebugBleObservableImpl: DebugBleObservable {
    override val myKey: BehaviorSubject<CenKey> = create()
    override val myCen: BehaviorSubject<Cen> = create()
    override val observedCens: BehaviorSubject<Cen> = create()

    override fun setMyKey(key: CenKey) {
        myKey.onNext(key)
    }

    override fun setMyCen(cen: Cen) {
        myCen.onNext(cen)
    }

    override fun setObservedCen(cen: Cen) {
        observedCens.onNext(cen)
    }
}
