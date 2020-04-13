package org.coepi.android.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import org.coepi.android.NonReferencedDependenciesActivator
import org.coepi.android.ble.BleEnabler
import org.coepi.android.ble.BleManager
import org.coepi.android.ble.BleManagerImpl
import org.coepi.android.ble.BlePreconditions
import org.coepi.android.ble.BlePreconditionsNotifier
import org.coepi.android.ble.BlePreconditionsNotifierImpl
import org.coepi.android.cen.CENModule
import org.coepi.android.cen.apiModule
import org.coepi.android.cross.CenReportsNotifier
import org.coepi.android.repo.repoModule
import org.coepi.android.system.Preferences
import org.coepi.android.system.Resources
import org.coepi.android.system.log.cachingLog
import org.coepi.android.ui.alerts.AlertsViewModel
import org.coepi.android.ui.debug.cen.CENViewModel
import org.coepi.android.ui.container.ContainerViewModel
import org.coepi.android.ui.debug.DebugViewModel
import org.coepi.android.ui.debug.logs.LogsViewModel
import org.coepi.android.ui.home.HomeViewModel
import org.coepi.android.ui.location.LocationViewModel
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.notifications.AppNotificationChannels
import org.coepi.android.ui.notifications.NotificationChannelsCreator
import org.coepi.android.ui.notifications.NotificationsShower
import org.coepi.android.ui.onboarding.OnboardingPermissionsChecker
import org.coepi.android.ui.onboarding.OnboardingShower
import org.coepi.android.ui.onboarding.OnboardingViewModel
import org.coepi.android.ui.settings.SettingsViewModel
import org.coepi.android.ui.symptoms.SymptomsViewModel
import org.coepi.android.worker.cenfetcher.ContactsFetchManager
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SymptomsViewModel(get(), get()) }
    viewModel { AlertsViewModel(get(), get()) }
    viewModel { SettingsViewModel() }
    viewModel { CENViewModel(get(), get(), get()) }
    viewModel { LocationViewModel() }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { LogsViewModel(cachingLog) }
    viewModel { DebugViewModel(get()) }
    viewModel { ContainerViewModel(get()) }
}

val systemModule = module {
    single { getSharedPrefs(androidApplication()) }
    single { Preferences(get()) }
    single { OnboardingPermissionsChecker() }
    single<BlePreconditionsNotifier> { BlePreconditionsNotifierImpl() }
    single { BlePreconditions(get(), get(), get()) }
    single { BleEnabler() }
    single { Resources(androidApplication()) }
    single<BleManager> { BleManagerImpl(androidApplication()) }
//    single<BleManager> { BleSimulator() }  // Disable BleManagerImpl and enable this to use BLE simulator
    single { NonReferencedDependenciesActivator(get(), get(), get()) }
    single { ContactsFetchManager(get()) }
    single { CenReportsNotifier(get(), get(), get(), get(), get()) }
}

val uiModule = module {
    single { OnboardingShower(get(), get()) }
    single { RootNavigation() }
    single { NotificationChannelsCreator(androidApplication()) }
    single { AppNotificationChannels(get(), get()) }
    single { NotificationsShower(get()) }
}

val appModule = listOf(
    repoModule,
    viewModelModule,
    systemModule,
    apiModule,
    CENModule,
    uiModule
)

fun getSharedPrefs(androidApplication: Application): SharedPreferences =
    androidApplication.getSharedPreferences("default", MODE_PRIVATE)
