package org.coepi.android.ui.extensions

import android.view.View
import android.widget.Adapter
import android.widget.AdapterView

fun <T: Adapter> AdapterView<T>.onItemSelected(callback: (Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            callback(position)
        }
        override fun onNothingSelected(parent: AdapterView<*>) {}
    }
}
