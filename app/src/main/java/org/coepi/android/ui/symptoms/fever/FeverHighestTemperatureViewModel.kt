package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.ViewModel
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.model.Temperature.Fahrenheit
import org.coepi.android.domain.model.Temperature.Celsius
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class FeverHighestTemperatureViewModel(
    val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {
    // TODO Hardcoded to use Fahrenheit, add ability to select scale
    fun onTempChanged(tempStr: String) {
        if (tempStr.isEmpty()) {
            symptomFlowManager.setFeverHighestTemperatureTaken(null)
        } else {
            val temperature: Float= tempStr.toFloatOrNull() ?: error("Invalid input: $tempStr")
            symptomFlowManager.setFeverHighestTemperatureTaken(Fahrenheit(temperature))
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
