package org.coepi.android.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeWith(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) {
    observe(lifecycleOwner, Observer { observer.invoke(it) })
}
