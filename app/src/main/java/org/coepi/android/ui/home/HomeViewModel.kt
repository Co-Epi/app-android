package org.coepi.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.extensions.toLiveData

class HomeViewModel : ViewModel() {

    val text: LiveData<String> = Observable.just("TODO location").toLiveData()
}
