package org.coepi.android.ui.symptoms.cough

import androidx.lifecycle.ViewModel
import org.coepi.android.repo.SymptomRepo
import org.coepi.android.system.Resources
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.symptoms.cough.CoughStatusFragmentDirections.Companion.actionGlobalCoughStatusFragment

class CoughDurationViewModel (
    private val symptomRepo: SymptomRepo,
    resources: Resources,
    uiNotifier: UINotifier,
    val navigation: RootNavigation
) : ViewModel(){

    fun onClickSubmit(){
        navigateNextScreen()
    }

    fun onClickUnknown(){
        navigateNextScreen()
    }

    fun onClickSkip(){
        navigateNextScreen()
    }

    private fun navigateNextScreen(){
        navigation.navigate(ToDestination(actionGlobalCoughStatusFragment()))
    }

    fun onBack(){
        navigation.navigate(Back)
    }
}