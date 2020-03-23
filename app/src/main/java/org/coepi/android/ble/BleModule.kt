package org.coepi.android.ble

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

// TODO bg service

val bleModule = module {
    single<BleDiscovery> { BleDiscoveryImpl(androidApplication()) }
    single { BlePeripheral(androidApplication()) }
}
