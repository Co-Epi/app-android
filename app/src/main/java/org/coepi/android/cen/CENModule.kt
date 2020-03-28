package org.coepi.android.cen

import org.koin.dsl.module

val CENModule = module {
    single { RealmCenDao(get()) }
    single { RealmCenReportDao(get()) }
    single { RealmCenKeyDao(get()) }
    single { CenRepo(get(), get(), get(), get()) }
}
