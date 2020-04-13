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
        debugBleObservable.myCen.asSequence().map { it.distinct() },
        debugBleObservable.observedCens.asSequence().map { it.distinct() }

    ).map { (keys, myCens, observedCens) ->
        listOf(Header("My key")) +
        keys.map { Item(it.key) } +
        listOf(Header("My CEN")) +
        myCens.map { Item(it.toHex()) } +
        listOf(Header("Discovered")) +
        observedCens.map { Item(it.toHex()) }
    }.toLiveData()
}
