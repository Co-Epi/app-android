package org.coepi.android.cen

import androidx.room.Room
import org.koin.dsl.module

val CENModule = module {
    single { get<AppDatabase>().cenDao() }
    single { get<AppDatabase>().cenReportDao() }
    single { get<AppDatabase>().cenkeyDao() }
    // TODO: REMOVE allowMainThreadQueries, fallbackToDestructiveMigration
    single { Room.databaseBuilder(get(), AppDatabase::class.java, "coepi_database").allowMainThreadQueries().fallbackToDestructiveMigration().build() }
    single { CENRepo(get(), get(), get(), get()) }
}
