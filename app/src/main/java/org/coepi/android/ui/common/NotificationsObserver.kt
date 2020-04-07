package org.coepi.android.ui.common

import android.app.Activity
import androidx.lifecycle.Observer

class NotificationsObserver(private val activity: Activity?) : Observer<UINotificationData> {
    override fun onChanged(notification: UINotificationData?) {
        notification?.let { notificationData ->
            activity?.let { activity ->
                UINotification().show(notificationData, activity)
            }
        }
    }
}
