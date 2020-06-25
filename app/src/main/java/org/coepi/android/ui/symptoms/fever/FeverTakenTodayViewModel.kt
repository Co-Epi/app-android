package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_HIGHEST_TEMPERATURE
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.core.domain.model.UserInput.Some

class FeverTakenTodayViewModel(
    private val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.submitSymptomsState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    // TODO toggle? Does it make sense to let user clear selection?

    fun onClickYes() {
        symptomFlowManager.setFeverTakenTemperatureToday(Some(true))
        symptomFlowManager.addUniqueStepAfterCurrent(FEVER_HIGHEST_TEMPERATURE)
        symptomFlowManager.navigateForward()
    }

    fun onClickNo() {
        symptomFlowManager.setFeverTakenTemperatureToday(Some(false))
        symptomFlowManager.removeIfPresent(FEVER_HIGHEST_TEMPERATURE)
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
