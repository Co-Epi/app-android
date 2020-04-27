package org.coepi.android.system.rx

/**
 * Represents the state of a long running operation
 * Can be used by UI to show progress indicator and success / error notifications
 */
sealed class OperationState<out T> {
    object NotStarted: OperationState<Nothing>()
    object Progress: OperationState<Nothing>()
    data class Success<out T>(val data: T): OperationState<T>()
    data class Failure(val t: Throwable): OperationState<Nothing>()

    fun <U>map(f: (T) -> U): OperationState<U> =
        when (this) {
            is NotStarted -> NotStarted
            is Success -> Success(f(data))
            is Progress -> Progress
            is Failure -> Failure(t)
        }

    fun <U>flatMap(f: (T) -> OperationState<U>): OperationState<U> =
        when (this) {
            is NotStarted -> NotStarted
            is Success -> f(data)
            is Progress -> Progress
            is Failure -> Failure(t)
        }
}

typealias VoidOperationState = OperationState<Unit>
