package org.coepi.android.cen

import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.repo.CoepiRepoImpl
import org.koin.dsl.module

val CENModule = module {
    single { RealmCenDao(get()) }
    single { RealmCenReportDao(get()) }
    single { RealmCenKeyDao(get()) }
    single { RealmCenLastKeysCheckDao(get()) }
    single<CenRepo> { CenRepoImpl(get(), get(), get(), get(), get()) }
    single<CoEpiRepo> { CoepiRepoImpl(get()) }
    single { CenManager(get(), get(), get(), get()) }
}
