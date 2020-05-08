package org.coepi.android.tcn

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Logger
import org.coepi.android.api.TcnApi
import org.coepi.android.common.ApiSymptomsMapper
import org.coepi.android.common.ApiSymptomsMapperImpl
import org.coepi.android.system.log.LogTag.NET
import org.coepi.android.system.log.log
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {
    single { provideRetrofit() }
    single { provideTcnApi(get()) }
    single<ApiSymptomsMapper> { ApiSymptomsMapperImpl(androidApplication(), get()) }
}

private fun provideRetrofit() : Retrofit {
    val myLog = log
    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(object: Logger {
            override fun log(message: String) {
                myLog.v(message, NET)
            }
        })
        .apply { setLevel(BODY) })
        .build()

    return Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://18ye1iivg6.execute-api.us-west-1.amazonaws.com/v4/")
        .client(client)
        .build()
}

private fun provideTcnApi(retrofit: Retrofit): TcnApi =
    retrofit.create(TcnApi::class.java)
