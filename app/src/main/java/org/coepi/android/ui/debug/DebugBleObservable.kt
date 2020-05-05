package org.coepi.android.ui.debug

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.create
import org.coepi.android.tcn.Tcn
import org.coepi.android.tcn.TcnKey

interface DebugBleObservable {
    val myKey: Observable<TcnKey>
    val myTcn: Observable<Tcn>
    val observedTcns: Observable<Tcn>

    fun setMyKey(key: TcnKey)
    fun setMyTcn(tcn: Tcn)
    fun setObservedTcn(tcn: Tcn)
}

class DebugBleObservableImpl: DebugBleObservable {
    override val myKey: BehaviorSubject<TcnKey> = create()
    override val myTcn: BehaviorSubject<Tcn> = create()
    override val observedTcns: BehaviorSubject<Tcn> = create()

    override fun setMyKey(key: TcnKey) {
        myKey.onNext(key)
    }

    override fun setMyTcn(tcn: Tcn) {
        myTcn.onNext(tcn)
    }

    override fun setObservedTcn(tcn: Tcn) {
        observedTcns.onNext(tcn)
    }
}
