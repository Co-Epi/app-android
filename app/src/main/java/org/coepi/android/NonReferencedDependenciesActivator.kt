package org.coepi.android

import org.coepi.android.cross.ScannedCensHandler
import org.coepi.android.ui.notifications.NotificationChannelsInitializer
import org.coepi.android.worker.ContactsFetchManager

class NonReferencedDependenciesActivator(
    scannedCensHandler: ScannedCensHandler,
    notificationChannelsInitializer: NotificationChannelsInitializer,
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
