package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.ViewModel
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.Temperature
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.symptoms.fever.FeverHighestTemperatureViewModel.Temperature.Fahrenheit

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
            symptomFlowManager.setFeverHighestTemperatureTaken(Temperature(Fahrenheit(temperature).value))
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

    sealed class Temperature {
        data class Celsius(val value: Float) : Temperature() {
            override fun toFarenheit(): Fahrenheit =
                Fahrenheit(value) // TODO convert
        }
        data class Fahrenheit(val value: Float) : Temperature() {
            override fun toCelsius(): Celsius =
                Celsius(value) // TODO convert
        }

        open fun toCelsius(): Celsius = when (this) {
            is Celsius -> this
            is Fahrenheit -> toCelsius()
        }

        open fun toFarenheit(): Fahrenheit = when (this) {
            is Fahrenheit -> this
            is Celsius -> toFarenheit()
        }
    }
}
