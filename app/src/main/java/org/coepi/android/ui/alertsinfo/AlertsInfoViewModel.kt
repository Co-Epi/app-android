package org.coepi.android.ui.alertsinfo

import android.net.Uri.parse
import androidx.lifecycle.ViewModel
import org.coepi.android.R.string.privacy_link
import org.coepi.android.system.Resources
import org.coepi.android.system.WebLaunchEventEmitter

class AlertsInfoViewModel(
    private val resources: Resources,
    private val webLaunchEventEmitter: WebLaunchEventEmitter
) : ViewModel() {

    fun onClickPrivacyLink() {
        webLaunchEventEmitter.launch(parse(resources.getString(privacy_link)))
    }
}
