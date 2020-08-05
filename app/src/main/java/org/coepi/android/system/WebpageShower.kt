package org.coepi.android.system

import android.app.Activity
import android.content.Intent
import android.net.Uri

class WebpageShower {
    fun show(activity: Activity, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
    }
}
