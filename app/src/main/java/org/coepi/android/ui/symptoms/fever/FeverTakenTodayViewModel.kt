package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.ViewModel
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class FeverTakenTodayViewModel (
    private val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    fun onClickYes() {
        symptomFlowManager.setFeverTakenTemperatureToday(true)
        symptomFlowManager.navigateForward()
    }

    fun onClickNo() {
        symptomFlowManager.setFeverTakenTemperatureToday(false)
        symptomFlowManager.navigateForward()
    }

    fun onClickSkip() {
        symptomFlowManager.navigateForward()
    }

    fun onBack() {
        symptomFlowManager.onBack()
    }

    fun onBackPressed() {
        onBack()
        navigation.navigate(Back)
    }
}
