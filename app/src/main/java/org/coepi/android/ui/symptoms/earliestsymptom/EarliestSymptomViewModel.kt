package org.coepi.android.ui.symptoms.earliestsymptom

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class EarliestSymptomViewModel(
    val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.sendReportState
        .toIsInProgress()
        .observeOn(AndroidSchedulers.mainThread())
        .toLiveData()


    fun onDurationChanged(durationStr: String) {
        if (durationStr.isEmpty()) {
            symptomFlowManager.setEarliestSymptomStartedDaysAgo(None)
        } else {
            val duration: Int = durationStr.toIntOrNull() ?: error("Invalid input: $durationStr")
            symptomFlowManager.setEarliestSymptomStartedDaysAgo(Some(duration))
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
