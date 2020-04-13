package org.coepi.android.system

import android.os.Build
import android.os.Build.VERSION.RELEASE
import org.coepi.android.BuildConfig.VERSION_CODE
import org.coepi.android.BuildConfig.VERSION_NAME

interface EnvInfos {
    val deviceName: String
    val appVersionName: String
    val appVersionCode: Int
    val osVersion: String
}

class EnvInfosImpl: EnvInfos {

    override val deviceName: String get() {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else "$manufacturer $model"
    }
    override val appVersionName: String get() = VERSION_NAME
    override val appVersionCode: Int get() = VERSION_CODE

    override val osVersion: String get() = RELEASE
}
