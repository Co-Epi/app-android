package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.ViewModel
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Armpit
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Ear
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Mouth
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalFeverTemperatureSpotInputFragment

class FeverTemperatureSpotViewModel (
    private val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    fun onClickMouth() {
        symptomFlowManager.setFeverTakenTemperatureSpot(Mouth)
        symptomFlowManager.navigateForward()
    }

    fun onClickEar() {
        symptomFlowManager.setFeverTakenTemperatureSpot(Ear)
        symptomFlowManager.navigateForward()
    }

    fun onClickArmpit() {
        symptomFlowManager.setFeverTakenTemperatureSpot(Armpit)
        symptomFlowManager.navigateForward()
    }

    fun onClickOther() {
        navigation.navigate(ToDestination(actionGlobalFeverTemperatureSpotInputFragment()))
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
