package org.coepi.android

import org.coepi.android.cross.ScannedCensHandler
import org.coepi.android.ui.notifications.AppNotificationChannels
import org.coepi.android.worker.cenfetcher.ContactsFetchManager

class NonReferencedDependenciesActivator(
    scannedCensHandler: ScannedCensHandler,
    notificationChannelsInitializer: AppNotificationChannels,
    contactsFetchManager: ContactsFetchManager
    ) {
    init {
        listOf(
            scannedCensHandler,
            notificationChannelsInitializer,
            contactsFetchManager
        ).forEach { it.toString() }
    }

    fun activate() {}
}
