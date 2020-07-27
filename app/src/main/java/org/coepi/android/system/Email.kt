package org.coepi.android.system

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_EMAIL
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.content.Intent.createChooser
import android.net.Uri
import org.coepi.android.system.log.log

interface Email {
    fun send(activity: Activity, recipient: String, subject: String, message: String = "")
}

class EmailImpl: Email {
    override fun send(activity: Activity, recipient: String, subject: String, message: String) {
        val intent = Intent(ACTION_SEND).apply {
            data = Uri.parse("mailto:")
            type = "message/rfc822"
            putExtra(EXTRA_EMAIL, arrayOf(recipient))
            putExtra(EXTRA_SUBJECT, subject)
            putExtra(EXTRA_TEXT, message)
        }
        try {
            activity.startActivity(createChooser(intent, "Choose Email Client..."))
        } catch (e: Exception) {
            log.e("Couldn't open email client: $e")
        }
    }
}
