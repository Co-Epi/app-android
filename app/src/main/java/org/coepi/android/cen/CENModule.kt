package org.coepi.android.cen

import org.coepi.android.cross.ScannedCensHandler
import org.coepi.android.domain.CenLogic
import org.coepi.android.domain.CenLogicImpl
import org.coepi.android.domain.CenMatcher
import org.coepi.android.domain.CenMatcherImpl
import org.coepi.android.domain.TcnGenerator
import org.coepi.android.domain.TcnGeneratorImpl
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.repo.CoepiRepoImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val CENModule = module {
    single<CenDao>(createdAtStart = true) { RealmCenDao(get()) }
    single<CenReportDao>(createdAtStart = true) { RealmCenReportDao(get()) }
    single<CenKeyDao>(createdAtStart = true) { RealmCenKeyDao(get()) }
    single<CenReportRepo> { CenReportRepoImpl(get(), get(), get()) }
    single<CenMatcher> { CenMatcherImpl(get()) }
    single<CenLogic> { CenLogicImpl() }
    single<CoEpiRepo> { CoepiRepoImpl(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    single<TcnGenerator> { TcnGeneratorImpl(androidApplication()) }
    single { ScannedCensHandler(get(), get(), get()) }
    single { BleInitializer(get(), get()) }
}
