package org.coepi.android.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.coepi.android.NotReferencedDependenciesActivator
import org.coepi.android.ble.BleEnabler
import org.coepi.android.ble.BleManager
import org.coepi.android.ble.BleManagerImpl
import org.coepi.android.ble.BlePreconditions
import org.coepi.android.ble.BlePreconditionsNotifier
import org.coepi.android.ble.BlePreconditionsNotifierImpl
import org.coepi.android.tcn.TcnModule
import org.coepi.android.tcn.apiModule
import org.coepi.android.repo.repoModule
import org.coepi.android.system.Clipboard
import org.coepi.android.system.ClipboardImpl
import org.coepi.android.system.EnvInfos
import org.coepi.android.system.EnvInfosImpl
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesImpl
import org.coepi.android.system.Resources
import org.coepi.android.system.intent.InfectionsNotificationIntentHandler
import org.coepi.android.system.intent.IntentForwarder
import org.coepi.android.system.intent.IntentForwarderImpl
import org.coepi.android.system.log.cachingLog
import org.coepi.android.ui.alerts.AlertsViewModel
import org.coepi.android.ui.alertsdetails.AlertsDetailsFragment
import org.coepi.android.ui.alertsdetails.AlertsDetailsViewModel
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.common.UINotifierImpl
import org.coepi.android.ui.container.ContainerViewModel
import org.coepi.android.ui.debug.DebugBleObservable
import org.coepi.android.ui.debug.DebugBleObservableImpl
import org.coepi.android.ui.debug.DebugViewModel
import org.coepi.android.ui.debug.ble.DebugBleViewModel
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
import org.coepi.android.ui.symptoms.breathless.BreathlessViewModel
import org.coepi.android.ui.symptoms.cough.CoughDurationViewModel
import org.coepi.android.ui.symptoms.cough.CoughStatusViewModel
import org.coepi.android.ui.symptoms.cough.CoughTypeViewModel
import org.coepi.android.ui.symptoms.earliestsymptom.EarliestSymptomViewModel
import org.coepi.android.ui.symptoms.fever.FeverDurationViewModel
import org.coepi.android.ui.symptoms.fever.FeverHighestTemperatureViewModel
import org.coepi.android.ui.symptoms.fever.FeverTakenTodayViewModel
import org.coepi.android.ui.symptoms.fever.FeverTemperatureSpotInputViewModel
import org.coepi.android.ui.symptoms.fever.FeverTemperatureSpotViewModel
import org.coepi.android.ui.thanks.ThanksViewModel
import org.coepi.android.worker.tcnfetcher.ContactsFetchManager
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SymptomsViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { ThanksViewModel(get()) }
    viewModel { AlertsViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel() }
    viewModel { LocationViewModel() }
    viewModel { OnboardingViewModel(get(), get()) }
    viewModel { LogsViewModel(cachingLog, get(), get(), get()) }
    viewModel { DebugViewModel(get()) }
    viewModel { DebugBleViewModel(get()) }
    viewModel { ContainerViewModel(get()) }
    viewModel { (args: AlertsDetailsFragment.Args) -> AlertsDetailsViewModel(args) }
    viewModel { CoughTypeViewModel(get(), get()) }
    viewModel { CoughDurationViewModel(get(), get()) }
    viewModel { CoughStatusViewModel(get(), get(), get()) }
    viewModel { EarliestSymptomViewModel(get(), get())}
    viewModel { BreathlessViewModel(get(), get(), get()) }
    viewModel { FeverDurationViewModel(get(), get()) }
    viewModel { FeverTakenTodayViewModel(get(), get()) }
    viewModel { FeverHighestTemperatureViewModel(get(), get()) }
    viewModel { FeverTemperatureSpotViewModel(get(), get()) }
    viewModel { FeverTemperatureSpotInputViewModel(get(), get()) }
}

val systemModule = module {
    single { getSharedPrefs(androidApplication()) }
    single<Preferences> { PreferencesImpl(get(), get()) }
    single { OnboardingPermissionsChecker() }
    single<BlePreconditionsNotifier> { BlePreconditionsNotifierImpl() }
    single { BlePreconditions(get(), get(), get()) }
    single { BleEnabler() }
    single { Resources(androidApplication()) }
    single<BleManager> { BleManagerImpl(androidApplication(), get()) }
//    single<BleManager> { BleSimulator() }  // Disable BleManagerImpl and enable this to use BLE simulator
    single { NotReferencedDependenciesActivator(get(), get(), get(), get()) }
    single { ContactsFetchManager(get()) }
    single<DebugBleObservable> { DebugBleObservableImpl() }
    single<Clipboard> { ClipboardImpl(get()) }
    single<EnvInfos> { EnvInfosImpl() }
    single<IntentForwarder> { IntentForwarderImpl() }
    single { InfectionsNotificationIntentHandler(get(), get()) }
    single<UINotifier> { UINotifierImpl() }
    single { provideGson() }
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
    TcnModule,
    uiModule
)

fun getSharedPrefs(androidApplication: Application): SharedPreferences =
    androidApplication.getSharedPreferences("default", MODE_PRIVATE)

private fun provideGson(): Gson = GsonBuilder()
    .serializeNulls()
    .setLenient()
    .create()
