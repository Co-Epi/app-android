package org.coepi.android.tcn

import org.coepi.android.cross.ScannedTcnsHandler
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShower
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShowerImpl
import org.koin.dsl.module

@OptIn(ExperimentalUnsignedTypes::class)
val TcnModule = module {
    single<AlertsDao>(createdAtStart = true) { RealmAlertDao(get()) }
    single<NewAlertsNotificationShower> { NewAlertsNotificationShowerImpl(get(), get(), get()) }
    single { ScannedTcnsHandler(get(), get(), get()) }
    single { BleInitializer(get(), get()) }
}
