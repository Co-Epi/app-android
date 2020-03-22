package org.coepi.android.di

import org.coepi.android.ui.ble.BleViewModel
import org.coepi.android.ui.care.CareViewModel
import org.coepi.android.ui.location.LocationViewModel
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingShower
import org.coepi.android.ui.onboarding.OnboardingViewModel
import org.coepi.android.ui.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CareViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { BleViewModel() }
    viewModel { LocationViewModel() }
    viewModel { OnboardingViewModel(get()) }
}

val systemModule = module {
    single { RootNavigation() }
    single { OnboardingShower(get()) }
}

val appModule = listOf(
    viewModelModule,
    systemModule
)
