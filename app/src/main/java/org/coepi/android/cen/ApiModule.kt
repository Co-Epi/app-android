package org.coepi.android.cen

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.coepi.android.api.CENApi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.SECONDS
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

val apiModule = module {
    single { provideRetrofit() }
    single { provideCENApi(get()) }
}

private fun provideRetrofit() : Retrofit {
    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { setLevel(BODY) })
        .build()

    return Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://q69c4m2myb.execute-api.us-west-2.amazonaws.com/v3/")
        .client(client)
        .build()
}

private fun provideCENApi(retrofit: Retrofit): CENApi =
    retrofit.create(CENApi::class.java)
