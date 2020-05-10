package org.coepi.android.system

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

class Resources(private val context: Context) {
    fun getString(id: Int, vararg args: Any): String = context.getString(id, *args)

    fun getDrawable(id: Int): Drawable = context.getDrawable(id)!!

    fun getQuantityString(id: Int, quantity: Int): String =
        context.resources.getQuantityString(id, quantity, quantity.toString())

}
