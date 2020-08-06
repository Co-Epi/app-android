package org.coepi.android.system

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri

interface WebLauncher {
    fun show(activity: Activity, uri: Uri)
}

class WebLauncherImpl : WebLauncher {
    override fun show(activity: Activity, uri: Uri) {
        val intent = Intent(ACTION_VIEW, uri)
        activity.startActivity(intent)
    }
}
