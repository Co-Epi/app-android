package org.coepi.android.ui.navigation

import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import java.io.Serializable

sealed class NavigationCommand : Serializable {
    data class ToDestination(val destinationId: NavDirections) : NavigationCommand()
    data class ToDirections(val directions: NavDirections) : NavigationCommand()
    object Back : NavigationCommand()
    data class BackTo(val destinationId: Int) : NavigationCommand()
}
