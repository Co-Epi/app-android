package org.coepi.android.system.rx

import io.reactivex.Notification
import io.reactivex.Observer
import io.reactivex.functions.Consumer
import org.coepi.android.system.rx.OperationState.Failure
import org.coepi.android.system.rx.OperationState.Success

/**
 * Forwards operation success/error to observer as VoidOperationState
 */
class OperationStateNotifier<T>(
    private val observer: Observer<OperationState<T>>
): Consumer<Notification<T>> {

    override fun accept(notification: Notification<T>?) {
        if (notification == null) { return }
        when {
            notification.isOnNext ->
                observer.onNext(notification.value?.let { value ->
                    Success(value)
                } ?: Failure(IllegalStateException("Value is null")) )
            notification.isOnError -> (notification.error ?: Throwable("Unknown error")).let { t ->
                observer.onNext(Failure(t))
            }
        }
    }
}

