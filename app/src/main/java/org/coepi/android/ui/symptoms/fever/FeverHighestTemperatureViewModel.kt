package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.domain.model.Temperature.Fahrenheit
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class FeverHighestTemperatureViewModel(
    val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.sendReportState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    // TODO Hardcoded to use Fahrenheit, add ability to select scale
    fun onTempChanged(tempStr: String) {
        if (tempStr.isEmpty()) {
            symptomFlowManager.setFeverHighestTemperatureTaken(None)
        } else {
            val temperature: Float= tempStr.toFloatOrNull() ?: error("Invalid input: $tempStr")
            symptomFlowManager.setFeverHighestTemperatureTaken(Some(Fahrenheit(temperature)))
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
