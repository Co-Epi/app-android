package org.coepi.android.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import org.coepi.android.ble.BlePeripheral
import org.coepi.android.network.apiModule
import org.coepi.android.repo.repoModule
import org.coepi.android.system.Preferences
import org.coepi.android.system.log.cachingLog
import org.coepi.android.ui.ble.BleViewModel
import org.coepi.android.ui.symptoms.SymptomsViewModel
import org.coepi.android.ui.container.TabsContainerViewModel
import org.coepi.android.ui.debug.logs.LogsViewModel
import org.coepi.android.ui.location.LocationViewModel
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingShower
import org.coepi.android.ui.onboarding.OnboardingViewModel
import org.coepi.android.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SymptomsViewModel(get()) }
    viewModel { SettingsViewModel() }
    viewModel { BleViewModel(get()) }
    viewModel { LocationViewModel() }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { LogsViewModel(cachingLog, get()) }
    viewModel { TabsContainerViewModel(get()) }
}

val systemModule = module {
    single { RootNavigation() }
    single { OnboardingShower(get(), get()) }
    single { BlePeripheral(androidApplication()) }
    single { getSharedPrefs(androidApplication()) }
    single { Preferences(get()) }
}

val appModule = listOf(
    viewModelModule,
    systemModule,
    apiModule,
    repoModule
)

fun getSharedPrefs(androidApplication: Application): SharedPreferences =
    androidApplication.getSharedPreferences("default", MODE_PRIVATE)
