package org.coepi.android.system.rx

import io.reactivex.Notification
import io.reactivex.Observer
import io.reactivex.functions.Consumer
import org.coepi.android.system.rx.VoidOperationState.Failure
import org.coepi.android.system.rx.VoidOperationState.Success

/**
 * Forwards operation success/error to observer as VoidOperationState
 */
class VoidOperationStateConsumer(
    private val observer: Observer<VoidOperationState>
): Consumer<Notification<Unit>> {

    override fun accept(notification: Notification<Unit>?) {
        if (notification == null) { return }
        when {
            notification.isOnNext -> observer.onNext(Success)
            notification.isOnError -> (notification.error ?: Throwable("Unknown error")).let { t ->
                observer.onNext(Failure(t))
            }
        }
    }
}
