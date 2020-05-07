package org.coepi.android.ui.extensions

import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.onBack(consume: Boolean = false, callback: () -> Unit) {
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback()
                if (!consume) {
                    findNavController().navigateUp()
                }
            }
        }
    )
}
