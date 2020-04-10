package org.coepi.android.ui.extensions.rx

import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.Failure
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationState.Success
import org.coepi.android.ui.common.UINotificationData

/**
 * Maps to error notifications and optionally a success notification for the operation state.
 */
fun <T> Observable<OperationState<T>>.toNotification(successMessage: String? = null): Observable<UINotificationData> =
    flatMap {
        when (it) {
            is Success -> successMessage?.let {
                just(UINotificationData.Success(successMessage))
            } ?: empty()
            is Failure -> just(UINotificationData.Failure(
                it.t.message ?: "Unknown error"
            ))
            is Progress -> empty<UINotificationData>()
        }
    }

fun Observable<UINotificationData>.filterFailure(): Observable<UINotificationData> =
    flatMap {
        when (it) {
            is UINotificationData.Failure -> just(UINotificationData.Failure(it.message))
            else -> empty()
        }
    }
