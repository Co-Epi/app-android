package org.coepi.android.ui.extensions

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.coepi.android.R


class ProgressBarViewExtension : LinearLayout {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER

        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.progress_indicator, this, true)

        this.isClickable = true
        this.elevation = 10000.0f
    }
}