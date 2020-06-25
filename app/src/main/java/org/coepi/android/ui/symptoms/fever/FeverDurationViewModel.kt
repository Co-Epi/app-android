package org.coepi.android.ui.symptoms.fever

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.core.domain.model.SymptomInputs.Fever.Days
import org.coepi.core.domain.model.UserInput.None
import org.coepi.core.domain.model.UserInput.Some

class FeverDurationViewModel(
    val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.submitSymptomsState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    fun onDurationChanged(durationStr: String) {
        if (durationStr.isEmpty()) {
            symptomFlowManager.setFeverDays(None)
        } else {
            val duration: Int = durationStr.toIntOrNull() ?: error("Invalid input: $durationStr")
            symptomFlowManager.setFeverDays(Some(Days(duration)))
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
