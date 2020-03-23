package org.coepi.android.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.extensions.toLiveData
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.SEEN_ONBOARDING
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class OnboardingViewModel(
    private val rootNav: RootNavigation,
    private val preferences: Preferences
) : ViewModel() {

    val text: LiveData<String> = Observable.just("TODO onboarding").toLiveData()

    fun onCloseClick() {
        preferences.putBoolean(SEEN_ONBOARDING, true)
        rootNav.navigate(Back)
    }
}
