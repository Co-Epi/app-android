package org.coepi.android.ui.extensions.rx

import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.system.rx.VoidOperationState.Failure
import org.coepi.android.system.rx.VoidOperationState.Progress
import org.coepi.android.system.rx.VoidOperationState.Success
import org.coepi.android.ui.common.UINotificationData

/**
 * Maps to error notifications and optionally a success notification for the operation state.
 */
fun Observable<VoidOperationState>.toNotification(successMessage: String? = null): Observable<UINotificationData> =
    flatMap {
        when (it) {
            is Success -> successMessage?.let {
                just(UINotificationData.Success(successMessage))
            }
            is Failure -> just(UINotificationData.Failure(
                it.t.message ?: "Unknown error"
            )
            )
            is Progress -> empty<UINotificationData>()
        }
    }


fun Observable<VoidOperationState>.toIsInProgress(): Observable<Boolean> =
    map { it is Progress }
