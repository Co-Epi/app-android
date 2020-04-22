package org.coepi.android.cen

import org.coepi.android.cross.ScannedCensHandler
import org.coepi.android.domain.CenLogic
import org.coepi.android.domain.CenLogicImpl
import org.coepi.android.domain.CenMatcher
import org.coepi.android.domain.CenMatcherImpl
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.repo.CoepiRepoImpl
import org.koin.dsl.module

val CENModule = module {
    single<CenDao>(createdAtStart = true) { RealmCenDao(get()) }
    single<CenReportDao> { RealmCenReportDao(get()) }
    single<CenKeyDao> { RealmCenKeyDao(get()) }
    single<CenReportRepo> { CenReportRepoImpl(get(), get(), get()) }
    single<CenMatcher> { CenMatcherImpl(get()) }
    single<CenLogic> { CenLogicImpl() }
    single<CoEpiRepo> { CoepiRepoImpl(get(), get(), get(), get(), get()) }
    single<MyCenProvider> { MyCenProviderImpl(get(), get(), get()) }
    single { ScannedCensHandler(get(), get()) }
    single { BleInitializer(get(), get(), get()) }
}
