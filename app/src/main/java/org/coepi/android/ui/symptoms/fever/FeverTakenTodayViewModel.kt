package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_HIGHEST_TEMPERATURE
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class FeverTakenTodayViewModel (
    private val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.sendReportState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    // TODO toggle? Does it make sense to let user clear selection?

    fun onClickYes() {
        symptomFlowManager.addUniqueStepAfterCurrent(FEVER_HIGHEST_TEMPERATURE)
        symptomFlowManager.setFeverTakenTemperatureToday(Some(true))
        symptomFlowManager.navigateForward()
    }

    fun onClickNo() {
        symptomFlowManager.removeIfPresent(FEVER_HIGHEST_TEMPERATURE)
        symptomFlowManager.setFeverTakenTemperatureToday(Some(false))
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
