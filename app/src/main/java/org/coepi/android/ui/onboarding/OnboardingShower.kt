package org.coepi.android.ui.onboarding

import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingFragmentDirections.Companion.actionGlobalOnboarding

class OnboardingShower(private val rootNavigation: RootNavigation) {

    fun showIfNeeded() {
        rootNavigation.navigate(ToDirections(actionGlobalOnboarding()))
    }
}
