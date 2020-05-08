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
        debugBleObservable.myKey.asSequence().map { it.distinct() },
        debugBleObservable.myTcn.asSequence().map { it.distinct() },
        debugBleObservable.observedTcns.asSequence().map { it.distinct() }

    ).map { (keys, myTcns, observedTcns) ->
        listOf(Header("My key")) +
        keys.map { Item(it.key) } +
        listOf(Header("My TCN")) +
        myTcns.map { Item(it.toHex()) } +
        listOf(Header("Discovered")) +
        observedTcns.map { Item(it.toHex()) }
    }.toLiveData()
}
