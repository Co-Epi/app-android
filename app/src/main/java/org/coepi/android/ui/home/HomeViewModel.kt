package org.coepi.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.extensions.toLiveData
import org.coepi.android.ui.symptoms.SymptomsFragmentDirections.Companion.actionGlobalSymptomsFragment
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation

class HomeViewModel(private val rootNav: RootNavigation) : ViewModel() {

    val text: LiveData<String> = Observable.just("TODO Home").toLiveData()

    fun onCheckInClick(){

        rootNav.navigate(ToDestination(actionGlobalSymptomsFragment()))
    }

    fun onSeeAlertsClick(){

    }
}
