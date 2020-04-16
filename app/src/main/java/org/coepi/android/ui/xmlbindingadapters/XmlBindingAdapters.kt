package org.coepi.android.ui.xmlbindingadapters

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.BindingAdapter

object XmlBindingAdapters {

    @JvmStatic
    @BindingAdapter("isVisible")
    fun setIsVisible(view: View, isVisible: Boolean?) {
        view.visibility = if (isVisible == true) VISIBLE else GONE
    }
}
