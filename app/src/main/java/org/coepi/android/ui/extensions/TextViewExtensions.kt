package org.coepi.android.ui.extensions

import android.widget.TextView
import androidx.core.widget.doOnTextChanged

fun TextView.onTextChanged(f: (String) -> Unit) {
    doOnTextChanged { text, _, _, _ ->
        f(text?.toString() ?: "")
    }
}
