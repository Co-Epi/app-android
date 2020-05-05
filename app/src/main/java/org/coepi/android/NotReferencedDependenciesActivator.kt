package org.coepi.android

import org.coepi.android.cross.ScannedTcnsHandler
import org.coepi.android.system.intent.InfectionsNotificationIntentHandler
import org.coepi.android.ui.notifications.AppNotificationChannels
import org.coepi.android.worker.tcnfetcher.ContactsFetchManager

class NotReferencedDependenciesActivator(
    scannedTcnsHandler: ScannedTcnsHandler,
    notificationChannelsInitializer: AppNotificationChannels,
    contactsFetchManager: ContactsFetchManager,
    infectionsNotificationIntentHandler: InfectionsNotificationIntentHandler
    ) {
    init {
        listOf(
            scannedTcnsHandler,
            notificationChannelsInitializer,
            contactsFetchManager,
            infectionsNotificationIntentHandler
        ).forEach { it.toString() }
    }

    fun activate() {}
}
