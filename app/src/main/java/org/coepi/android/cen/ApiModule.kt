package org.coepi.android.cen

import org.coepi.android.api.CENApi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {
    single { provideRetrofit() }
    single { provideCENApi(get()) }
}

private fun provideRetrofit() : Retrofit = Retrofit.Builder()
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("https://coepi.wolk.com:8080")
    .build()

private fun provideCENApi(retrofit: Retrofit): CENApi =
    retrofit.create(CENApi::class.java)
