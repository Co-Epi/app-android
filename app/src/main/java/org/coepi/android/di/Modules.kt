package org.coepi.android.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import org.coepi.android.cen.apiModule
import org.coepi.android.cen.repoModule
import org.coepi.android.system.Preferences
import org.coepi.android.system.log.cachingLog
import org.coepi.android.ui.cen.CENViewModel
import org.coepi.android.ui.care.CareViewModel
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
    viewModel { CareViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { CENViewModel(get(), get()) }
    viewModel { LocationViewModel() }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { LogsViewModel(cachingLog, get(), get()) }
    viewModel { TabsContainerViewModel(get()) }
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
    apiModule
)

fun getSharedPrefs(androidApplication: Application): SharedPreferences =
    androidApplication.getSharedPreferences("default", MODE_PRIVATE)
