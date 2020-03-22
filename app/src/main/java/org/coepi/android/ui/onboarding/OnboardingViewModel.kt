package org.coepi.android.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.extensions.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class OnboardingViewModel(private val rootNav: RootNavigation) : ViewModel() {

    val text: LiveData<String> = Observable.just("TODO onboarding").toLiveData()

    fun onCloseClick() {
        rootNav.navigate(Back)
    }
}
