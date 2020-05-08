package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.ViewModel
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.Temperature
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class FeverHighestTemperatureViewModel(
    val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    fun onTempChanged(tempStr: String) {
        if (tempStr.isEmpty()) {
            symptomFlowManager.setFeverHighestTemperatureTaken(null)
        } else {
            val temperature: Float = tempStr.toFloatOrNull() ?: error("Invalid input: $tempStr")
            symptomFlowManager.setFeverHighestTemperatureTaken(Temperature(temperature))
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

    private fun cToF(c: Double) = (9/5.0 * c) + 32
    private fun fToC(f: Double) = 5/9.0 * (f - 32)
}
