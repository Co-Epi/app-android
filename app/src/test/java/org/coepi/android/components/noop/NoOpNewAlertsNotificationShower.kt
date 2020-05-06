package org.coepi.android.components.noop

import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShower

class NoOpNewAlertsNotificationShower: NewAlertsNotificationShower {
    override fun showNotification(newAlertsCount: Int) {}
}
