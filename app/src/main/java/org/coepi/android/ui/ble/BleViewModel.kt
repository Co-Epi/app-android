package org.coepi.android.ui.ble

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.extensions.toLiveData

class BleViewModel : ViewModel() {

    val text: LiveData<String> = Observable.just("TODO BLE").toLiveData()
}
