package org.coepi.android.ui.debug.ble

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxkotlin.Observables.combineLatest
import org.coepi.android.extensions.rx.asSequence
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.debug.DebugBleObservable
import org.coepi.android.ui.debug.ble.DebugBleItemViewData.Header
import org.coepi.android.ui.debug.ble.DebugBleItemViewData.Item

class DebugBleViewModel(debugBleObservable: DebugBleObservable) : ViewModel() {

    val items: LiveData<List<DebugBleItemViewData>> = combineLatest(
        debugBleObservable.myTcn.asSequence().map { it.distinct() },
        debugBleObservable.observedTcns.asSequence().map { it.distinct() }

    ).map { (myTcns, observedTcns) ->
        listOf(Header("My TCN")) +
        myTcns.map { Item(it.toHex()) } +
        listOf(Header("Discovered")) +
        observedTcns.map { Item(it.toHex()) }
    }.toLiveData()
}
