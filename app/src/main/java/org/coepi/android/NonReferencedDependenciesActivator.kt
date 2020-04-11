package org.coepi.android

import org.coepi.android.cross.ScannedCensHandler
import org.coepi.android.ui.notifications.NotificationChannelsInitializer

class NonReferencedDependenciesActivator(
    scannedCensHandler: ScannedCensHandler,
    notificationChannelsInitializer: NotificationChannelsInitializer
) {
    init {
        listOf(
            scannedCensHandler,
            notificationChannelsInitializer
        ).forEach { it.toString() }
    }

    fun activate() {}
}
