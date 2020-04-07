package org.coepi.android.system.rx

/**
 * Represents the state of a long running operation
 * Can be used by UI to show progress indicator and success / error notifications
 */
sealed class VoidOperationState {
    object Progress: VoidOperationState()
    object Success: VoidOperationState()
    data class Failure(val t: Throwable): VoidOperationState()
}
