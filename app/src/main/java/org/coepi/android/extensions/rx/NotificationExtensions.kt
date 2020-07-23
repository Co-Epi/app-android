package org.coepi.android.extensions.rx

import io.reactivex.Notification
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.Failure

fun <T>Notification<T>.toOperationState(): OperationState<T>? =
    when {
        isOnNext ->
            value?.let { value ->
                OperationState.Success(value)
            } ?: Failure(IllegalStateException("Value is null"))
        isOnError ->
            Failure(error ?: Throwable("Unknown error"))
        else -> null
    }
