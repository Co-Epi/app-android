package org.coepi.android.cen

import org.coepi.android.cross.ScannedCensHandler
import org.coepi.android.domain.CenLogic
import org.coepi.android.domain.CenLogicImpl
import org.coepi.android.domain.CenMatcher
import org.coepi.android.domain.CenMatcherImpl
import org.coepi.android.domain.TcnGenerator
import org.coepi.android.domain.TcnGeneratorImpl
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShower
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShowerImpl
import org.coepi.android.repo.reportsupdate.ReportsUpdater
import org.coepi.android.repo.reportsupdate.ReportsUpdaterImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val CENModule = module {
    single<CenDao>(createdAtStart = true) { RealmCenDao(get()) }
    single<CenReportDao>(createdAtStart = true) { RealmCenReportDao(get()) }
    single<CenKeyDao>(createdAtStart = true) { RealmCenKeyDao(get()) }
    single<CenReportRepo> { CenReportRepoImpl(get(), get(), get()) }
    single<ReportsUpdater> { ReportsUpdaterImpl(get(), get(), get(), get(), get(), get()) }
    single<NewAlertsNotificationShower> { NewAlertsNotificationShowerImpl(get(), get(), get()) }
    single<CenMatcher> { CenMatcherImpl() }
    single<CenLogic> { CenLogicImpl() }
    single<TcnGenerator> { TcnGeneratorImpl(androidApplication()) }
    single { ScannedCensHandler(get(), get(), get()) }
    single { BleInitializer(get(), get()) }
}
