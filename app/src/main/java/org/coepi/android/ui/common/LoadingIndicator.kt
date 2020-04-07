package org.coepi.android.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window.FEATURE_NO_TITLE
import androidx.lifecycle.Observer
import org.coepi.android.R.layout.progress_indicator

class ProgressObserver(private val context: Context) : Observer<Boolean> {
    private var dialog: Dialog? = null

    override fun onChanged(show: Boolean?) {
        if (show == true) {
            dialog = ProgressDialog(context).apply { show() }
        } else {
            dialog?.dismiss()
        }
    }
}

class ProgressDialog(context: Context) : Dialog(context) {
    init {
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(TRANSPARENT))
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(FEATURE_NO_TITLE)
        setContentView(progress_indicator)
    }
}
