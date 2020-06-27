package org.coepi.android.ui.debug

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.create
import org.coepi.core.domain.model.Tcn

interface DebugBleObservable {
    val myTcn: Observable<Tcn>
    val observedTcns: Observable<Tcn>

    fun setMyTcn(tcn: Tcn)
    fun setObservedTcn(tcn: Tcn)
}

class DebugBleObservableImpl: DebugBleObservable {
    override val myTcn: BehaviorSubject<Tcn> = create()
    override val observedTcns: BehaviorSubject<Tcn> = create()

    override fun setMyTcn(tcn: Tcn) {
        myTcn.onNext(tcn)
    }

    override fun setObservedTcn(tcn: Tcn) {
        observedTcns.onNext(tcn)
    }
}
