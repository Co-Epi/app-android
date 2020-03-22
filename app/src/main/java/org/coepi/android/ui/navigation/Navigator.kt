package org.coepi.android.ui.navigation

import androidx.navigation.NavController
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.BackTo
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections

class Navigator(private val navController: NavController) {

    fun navigate(command: NavigationCommand) {
        when (command) {
            is ToDirections -> navController.navigate(command.directions)
            is ToDestination -> navController.navigate(command.destinationId)
            is Back -> navController.popBackStack()
            is BackTo -> navController.popBackStack(command.destinationId, false)
        }
    }
}
