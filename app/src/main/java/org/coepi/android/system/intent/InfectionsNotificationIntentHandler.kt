package org.coepi.android.system.intent

import android.content.Intent
import org.coepi.android.system.intent.IntentKey.NOTIFICATION_INFECTION_ARGS
import org.coepi.android.system.log.log
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlerts
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation

class InfectionsNotificationIntentHandler(
    private val navigation: RootNavigation,
    compositeIntentHandler: IntentForwarder
): IntentHandler {

    init {
        compositeIntentHandler.register(this)
    }

    override fun handle(intent: Intent) {
        if (!intent.hasExtra(NOTIFICATION_INFECTION_ARGS.toString())) return
        log.d("Opened infection notification, navigating to alerts")

        navigation.navigate(ToDestination(actionGlobalAlerts()))
    }
}
