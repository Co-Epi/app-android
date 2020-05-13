package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomInputs.Fever.TemperatureSpot.Other
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class FeverTemperatureSpotInputViewModel(
    val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.sendReportState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    fun onlocationChanged(locationStr: String) {
        if (locationStr.isEmpty()) {
            symptomFlowManager.setFeverTakenTemperatureSpot(None)
        } else {
            symptomFlowManager.setFeverTakenTemperatureSpot(Some(Other(locationStr)))
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
