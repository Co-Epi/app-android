package org.coepi.android.extensions

import retrofit2.Response
import org.coepi.android.common.Result
import org.coepi.android.common.Result.Failure
import org.coepi.android.common.Result.Success

fun <T> Response<T>.toResult(): Result<T, Throwable> = when {
    isSuccessful ->
        body()?.let {
            Success(it)
        } ?: Failure(Throwable("Invalid state: Response doesn't have body: $this"))
    else ->
        errorBody()?.let {
            Failure(Throwable(it.string()))
        } ?: Failure(Throwable("Error response without error body"))
}
