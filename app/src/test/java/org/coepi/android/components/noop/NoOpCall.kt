package org.coepi.android.components.noop

import okhttp3.Request
import okhttp3.Request.Builder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoOpCall<T> : Call<T> {
    override fun enqueue(callback: Callback<T>) {}
    override fun isExecuted(): Boolean = false
    override fun clone(): Call<T> =
        NoOpCall()
    override fun isCanceled(): Boolean = false
    override fun cancel() {}
    override fun execute(): Response<T> =
        Response.success(null)
    override fun request(): Request = Builder()
        .build()
}
