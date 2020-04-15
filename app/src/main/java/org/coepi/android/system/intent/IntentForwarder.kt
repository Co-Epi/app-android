package org.coepi.android.system.intent

import android.content.Intent

interface IntentForwarder {
    fun onActivityCreated(intent: Intent)
    fun onNewIntent(intent: Intent?)

    fun register(intentHandler: IntentHandler)
}

class IntentForwarderImpl: IntentForwarder {
    private var handlers: List<IntentHandler> = emptyList()

    override fun onActivityCreated(intent: Intent) {
        handle(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        intent?.let { handle(it) }
    }

    @Synchronized
    private fun handle(intent: Intent) {
        handlers.forEach { it.handle(intent) }
    }

    @Synchronized
    override fun register(intentHandler: IntentHandler) {
        handlers = handlers + intentHandler
    }
}
