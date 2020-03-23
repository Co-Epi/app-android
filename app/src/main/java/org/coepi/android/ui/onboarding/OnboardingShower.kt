package org.coepi.android.ui.onboarding

import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.SEEN_ONBOARDING
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.onboarding.OnboardingFragmentDirections.Companion.actionGlobalOnboarding

class OnboardingShower(
    private val rootNavigation: RootNavigation,
    private val preferences: Preferences
) {
    fun showIfNeeded() {
        if (preferences.getBoolean(SEEN_ONBOARDING).not()) {
            rootNavigation.navigate(ToDirections(actionGlobalOnboarding()))
        }
    }
}
