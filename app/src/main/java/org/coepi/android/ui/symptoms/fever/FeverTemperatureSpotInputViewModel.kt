package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.ViewModel
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Other
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class FeverTemperatureSpotInputViewModel(
    val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    fun onlocationChanged(locationStr: String) {
        if (locationStr.isEmpty()) {
            symptomFlowManager.setFeverTakenTemperatureSpot(null)
        } else {
            symptomFlowManager.setFeverTakenTemperatureSpot(Other(locationStr))
        }
    }

    fun onClickSubmit() {
        symptomFlowManager.navigateForward()
    }

    fun onClickUnknown() {
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
