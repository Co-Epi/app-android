package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.core.domain.model.SymptomInputs.Fever.TemperatureSpot
import org.coepi.core.domain.model.SymptomInputs.Fever.TemperatureSpot.Armpit
import org.coepi.core.domain.model.SymptomInputs.Fever.TemperatureSpot.Ear
import org.coepi.core.domain.model.SymptomInputs.Fever.TemperatureSpot.Mouth
import org.coepi.core.domain.model.SymptomInputs.Fever.TemperatureSpot.Other
import org.coepi.core.domain.model.UserInput.Some

class FeverTemperatureSpotViewModel (
    private val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.submitSymptomsState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    fun onClickMouth() {
        onSelectSpot(Mouth)
    }

    fun onClickEar() {
        onSelectSpot(Ear)
    }

    fun onClickArmpit() {
        onSelectSpot(Armpit)
    }

    fun onClickOther() {
        onSelectSpot(Other)
    }

    private fun onSelectSpot(spot: TemperatureSpot) {
        symptomFlowManager.setFeverTakenTemperatureSpot(Some(spot))
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
