package org.coepi.android.ui.symptoms.cough

import androidx.lifecycle.ViewModel
import org.coepi.android.repo.SymptomRepo
import org.coepi.android.system.Resources
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class CoughTypeViewModel (
    private val symptomRepo: SymptomRepo,
    resources: Resources,
    uiNotifier: UINotifier,
    val navigation: RootNavigation
) : ViewModel(){

    fun onClickWet(){

    }

    fun onClickDry(){

    }

    fun onClickSkip(){

    }

    fun onBack(){
        navigation.navigate(Back)
    }
}