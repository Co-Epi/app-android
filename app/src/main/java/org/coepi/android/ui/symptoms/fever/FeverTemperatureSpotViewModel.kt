package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalFeverTemperatureSpotInputFragment
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Armpit
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Ear
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Mouth
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation

class FeverTemperatureSpotViewModel (
    private val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.submitSymptomsState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    fun onClickMouth() {
        symptomFlowManager.setFeverTakenTemperatureSpot(Some(Mouth))
        symptomFlowManager.navigateForward()
    }

    fun onClickEar() {
        symptomFlowManager.setFeverTakenTemperatureSpot(Some(Ear))
        symptomFlowManager.navigateForward()
    }

    fun onClickArmpit() {
        symptomFlowManager.setFeverTakenTemperatureSpot(Some(Armpit))
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
