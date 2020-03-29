package org.coepi.android

import android.app.Application
import org.coepi.android.di.appModule
import org.coepi.android.ble.covidwatch.BLEAdvertiser
import org.coepi.android.ble.covidwatch.BLEScanner
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    var bleAdvertiser: BLEAdvertiser? = null
    var bleScanner: BLEScanner? = null

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
    }
}
