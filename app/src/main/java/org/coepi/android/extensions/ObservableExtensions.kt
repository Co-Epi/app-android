package org.coepi.android.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams.fromPublisher
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread

fun <T> Observable<T>.toLiveData(): LiveData<T> =
    fromPublisher(observeOn(mainThread()).toFlowable(BUFFER))
