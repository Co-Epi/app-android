package org.coepi.android.di

import org.coepi.android.ble.BlePeripheral
import org.coepi.android.network.apiModule
import org.coepi.android.ui.ble.BleViewModel
import org.coepi.android.ui.care.CareViewModel
import org.coepi.android.ui.location.LocationViewModel
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingShower
import org.coepi.android.ui.onboarding.OnboardingViewModel
import org.coepi.android.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CareViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { BleViewModel(get()) }
    viewModel { LocationViewModel() }
    viewModel { OnboardingViewModel(get()) }
}

val systemModule = module {
    single { RootNavigation() }
    single { OnboardingShower(get()) }
    single { BlePeripheral(androidApplication()) }
}

val appModule = listOf(
    viewModelModule,
    systemModule,
    apiModule
)
