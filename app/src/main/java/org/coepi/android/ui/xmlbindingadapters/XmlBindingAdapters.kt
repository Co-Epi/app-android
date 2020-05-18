package org.coepi.android.ui.xmlbindingadapters

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.databinding.BindingAdapter

object XmlBindingAdapters {

    @JvmStatic
    @BindingAdapter("isVisible")
    fun setIsVisible(view: View, isVisible: Boolean?) {
        view.visibility = if (isVisible == true) VISIBLE else GONE
    }

    @JvmStatic
    @BindingAdapter("isClickable")
    fun setIsClickable(view: View, isClickable: Boolean?) {
        view.isClickable = !(isClickable == true)
    }

    @JvmStatic
    @BindingAdapter("android:src")
    fun setImageResource(imageView: ImageView, resource: Int) {
        imageView.setImageResource(resource)
    }
}
