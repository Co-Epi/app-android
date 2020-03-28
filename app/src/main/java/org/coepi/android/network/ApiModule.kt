package org.coepi.android.network

import org.coepi.android.network.api.ExposureApi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val apiModuleNetwork = module {
    single { provideRetrofit() }
    single { provideExposureApi(get()) }
}

private fun provideRetrofit() : Retrofit = Retrofit.Builder()
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl("")
    .build()

private fun provideExposureApi(retrofit: Retrofit): ExposureApi =
    retrofit.create(ExposureApi::class.java)
