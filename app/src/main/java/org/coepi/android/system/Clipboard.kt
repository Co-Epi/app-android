package org.coepi.android.system

import android.content.ClipData.newPlainText
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE

interface Clipboard {
    fun putInClipboard(text: String)
}

class ClipboardImpl(context: Context): Clipboard {
    private val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

    override fun putInClipboard(text: String) {
        clipboard.setPrimaryClip(newPlainText("Text", text))
    }
}
