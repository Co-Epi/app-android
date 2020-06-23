package org.coepi.android.tcn

import org.coepi.android.cross.ScannedTcnsHandler
import org.coepi.android.domain.TcnMatcher
import org.coepi.android.domain.TcnMatcherImpl
import org.coepi.android.domain.TcnGenerator
import org.coepi.android.domain.TcnGeneratorImpl
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShower
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShowerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

@OptIn(ExperimentalUnsignedTypes::class)
val TcnModule = module {
    single<TcnDao>(createdAtStart = true) { RealmTcnDao(get()) }
    single<AlertsDao>(createdAtStart = true) { RealmAlertDao(get()) }
    single<TcnKeyDao>(createdAtStart = true) { RealmTcnKeyDao(get()) }
    single<NewAlertsNotificationShower> { NewAlertsNotificationShowerImpl(get(), get(), get()) }
    single<TcnMatcher> { TcnMatcherImpl() }
    single<TcnGenerator> { TcnGeneratorImpl(androidApplication()) }
    single { ScannedTcnsHandler(get(), get(), get()) }
    single { BleInitializer(get(), get()) }
}
