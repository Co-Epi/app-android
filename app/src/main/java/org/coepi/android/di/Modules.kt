package org.coepi.android.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import org.coepi.android.ble.bleModule
import org.coepi.android.ble.bleSimulatorModule
import org.coepi.android.cen.CENModule
import org.coepi.android.cen.apiModule
import org.coepi.android.repo.repoModule
import org.coepi.android.system.Preferences
import org.coepi.android.system.log.cachingLog
import org.coepi.android.ui.cen.CENViewModel
import org.coepi.android.ui.symptoms.SymptomsViewModel
import org.coepi.android.ui.container.ContainerViewModel
import org.coepi.android.ui.debug.logs.LogsViewModel
import org.coepi.android.ui.location.LocationViewModel
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingShower
import org.coepi.android.ui.onboarding.OnboardingViewModel
import org.coepi.android.ui.settings.SettingsViewModel
import org.coepi.android.ui.home.HomeViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SymptomsViewModel(get()) }
    viewModel { SettingsViewModel() }
    viewModel { CENViewModel(get(), get(), get()) }
    viewModel { LocationViewModel() }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { LogsViewModel(cachingLog, get(), get()) }
    viewModel { ContainerViewModel(get()) }
}

val systemModule = module {
    single { RootNavigation() }
    single { OnboardingShower(get(), get()) }
    single { getSharedPrefs(androidApplication()) }
    single { Preferences(get()) }
}

val appModule = listOf(
    repoModule,
    viewModelModule,
    systemModule,
    apiModule,
    CENModule,
    bleModule
//    bleSimulatorModule // Disable bleModule and enable this to use BLE simulator
)

fun getSharedPrefs(androidApplication: Application): SharedPreferences =
    androidApplication.getSharedPreferences("default", MODE_PRIVATE)
