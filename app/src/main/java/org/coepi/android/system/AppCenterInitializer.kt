package org.coepi.android.system

import android.app.Application
import com.microsoft.appcenter.AppCenter.start
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import org.coepi.android.BuildConfig.DEBUG

interface AppCenterInitializer {
    fun onActivityCreated()
}

class AppCenterInitializerImpl(
    private val application: Application
) : AppCenterInitializer {

    override fun onActivityCreated() {
        if (!DEBUG) {
            init()
        }
    }

    fun init() {
        start(
            application,
            "1c70b9e6-0458-4205-8bc3-8df5c5d29a0c",
            Analytics::class.java,
            Crashes::class.java
        )
    }
}
