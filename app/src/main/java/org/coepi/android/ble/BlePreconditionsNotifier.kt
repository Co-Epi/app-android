package org.coepi.android.ble

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

interface BlePreconditionsNotifier {
    val bleEnabled: Observable<Unit>

    fun notifyBleEnabled()
}

class BlePreconditionsNotifierImpl: BlePreconditionsNotifier {

    override val bleEnabled: BehaviorSubject<Unit> = BehaviorSubject.create<Unit>()

    override fun notifyBleEnabled() {
        bleEnabled.onNext(Unit)
    }
}
