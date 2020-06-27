package org.coepi.android.ui.symptoms.cough

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.core.domain.model.SymptomInputs.Cough.Type.DRY
import org.coepi.core.domain.model.SymptomInputs.Cough.Type.WET
import org.coepi.core.domain.model.UserInput.Some

class CoughTypeViewModel(
    private val navigation: RootNavigation,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.submitSymptomsState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    fun onClickWet() {
        symptomFlowManager.setCoughType(Some(WET))
        symptomFlowManager.navigateForward()
    }

    fun onClickDry() {
        symptomFlowManager.setCoughType(Some(DRY))
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
