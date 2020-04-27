package org.coepi.android.extensions.retrofit

import retrofit2.Call
import org.coepi.android.common.Result
import org.coepi.android.common.Result.Failure
import org.coepi.android.common.Result.Success
import retrofit2.Response

fun <T> Call<T>.executeSafe(): Result<Response<T>, Throwable> =
    try {
        Success(execute())
    } catch (t: Throwable) {
        Failure(t)
    }
