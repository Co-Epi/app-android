package org.coepi.android.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.extensions.rx.toLiveData

class SettingsViewModel : ViewModel() {

    val text: LiveData<String> = Observable.just("TODO settings").toLiveData()
}
