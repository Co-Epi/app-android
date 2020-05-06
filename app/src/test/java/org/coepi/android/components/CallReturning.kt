package org.coepi.android.components

import okhttp3.Request
import okhttp3.Request.Builder
import org.coepi.android.components.noop.NoOpCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallReturning<T>(private val obj: T) : Call<T> {
    override fun enqueue(callback: Callback<T>) {}
    override fun isExecuted(): Boolean = false
    override fun clone(): Call<T> =
        NoOpCall()
    override fun isCanceled(): Boolean = false
    override fun cancel() {}
    override fun execute(): Response<T> =
        Response.success(obj)
    override fun request(): Request = Builder()
        .build()
}
