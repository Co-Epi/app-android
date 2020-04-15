package org.coepi.android

import org.coepi.android.cross.ScannedCensHandler
import org.coepi.android.system.intent.InfectionsNotificationIntentHandler
import org.coepi.android.ui.notifications.AppNotificationChannels
import org.coepi.android.worker.cenfetcher.ContactsFetchManager

class NonReferencedDependenciesActivator(
    scannedCensHandler: ScannedCensHandler,
    notificationChannelsInitializer: AppNotificationChannels,
    contactsFetchManager: ContactsFetchManager,
    infectionsNotificationIntentHandler: InfectionsNotificationIntentHandler
    ) {
    init {
        listOf(
            scannedCensHandler,
            notificationChannelsInitializer,
            contactsFetchManager,
            infectionsNotificationIntentHandler
        ).forEach { it.toString() }
    }

    fun activate() {}
}
