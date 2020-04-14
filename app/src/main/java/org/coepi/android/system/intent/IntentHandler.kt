package org.coepi.android.system.intent

import android.content.Intent

interface IntentHandler {
    fun handle(intent: Intent)
}
