package org.coepi.android.system

import android.util.DisplayMetrics
import android.util.DisplayMetrics.DENSITY_DEFAULT

class ScreenUnitsConverter(private val displayMetrics: DisplayMetrics) {
    fun dpToPixel(dp: Float): Float =
        dp * (displayMetrics.densityDpi.toFloat() / DENSITY_DEFAULT)

    fun pixelsToDp(px: Float): Float =
        px / (displayMetrics.densityDpi.toFloat() / DENSITY_DEFAULT)
}
