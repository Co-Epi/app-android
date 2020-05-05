package org.coepi.android.tcn

import org.coepi.android.cross.ScannedTcnsHandler
import org.coepi.android.domain.TcnMatcher
import org.coepi.android.domain.TcnMatcherImpl
import org.coepi.android.domain.TcnGenerator
import org.coepi.android.domain.TcnGeneratorImpl
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShower
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShowerImpl
import org.coepi.android.repo.reportsupdate.ReportsUpdater
import org.coepi.android.repo.reportsupdate.ReportsUpdaterImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val TcnModule = module {
    single<TcnDao>(createdAtStart = true) { RealmTcnDao(get()) }
    single<TcnReportDao>(createdAtStart = true) { RealmTcnReportDao(get()) }
    single<TcnKeyDao>(createdAtStart = true) { RealmTcnKeyDao(get()) }
    single<TcnReportRepo> { TcnReportRepoImpl(get(), get(), get()) }
    single<ReportsUpdater> { ReportsUpdaterImpl(get(), get(), get(), get(), get(), get()) }
    single<NewAlertsNotificationShower> { NewAlertsNotificationShowerImpl(get(), get(), get()) }
    single<TcnMatcher> { TcnMatcherImpl() }
    single<TcnGenerator> { TcnGeneratorImpl(androidApplication()) }
    single { ScannedTcnsHandler(get(), get(), get()) }
    single { BleInitializer(get(), get()) }
}
