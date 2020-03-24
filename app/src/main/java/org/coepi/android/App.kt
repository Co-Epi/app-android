package org.coepi.android

import android.app.Application
import android.content.Intent
import org.coepi.android.ble.BleDiscoveryImpl
import org.coepi.android.ble.BlePeripheral
import org.coepi.android.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}
