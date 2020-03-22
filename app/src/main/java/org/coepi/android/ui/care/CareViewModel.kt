package org.coepi.android.ui.care

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.extensions.toLiveData

class CareViewModel : ViewModel() {

    val text: LiveData<String> = Observable.just("TODO care").toLiveData()
}
