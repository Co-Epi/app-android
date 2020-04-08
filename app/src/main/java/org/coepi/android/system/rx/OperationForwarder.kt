package org.coepi.android.system.rx

import io.reactivex.Observer
import io.reactivex.functions.Consumer

class OperationForwarder<T>(
    private val observer: Observer<OperationState<T>>
): Consumer<OperationState<T>> {

    override fun accept(operationState: OperationState<T>?) {
        if (operationState == null) { return }
        observer.onNext(operationState)
    }
}
