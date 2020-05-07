package org.coepi.android.ui.symptoms.cough

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import org.coepi.android.R.string.symptom_report_cough_status_option_better_worse_day
import org.coepi.android.R.string.symptom_report_cough_status_option_same_or_steadily_worse
import org.coepi.android.R.string.symptom_report_cough_status_option_worse_outside
import org.coepi.android.common.Optional
import org.coepi.android.common.Optional.None
import org.coepi.android.common.toNullable
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status.BETTER_AND_WORSE_THROUGH_DAY
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status.SAME_OR_STEADILY_WORSE
import org.coepi.android.domain.symptomflow.SymptomInputs.Cough.Status.WORSE_WHEN_OUTSIDE
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.Resources
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class CoughStatusViewModel (
    private val navigation: RootNavigation,
    private val resources: Resources,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    private val selectedStatus: BehaviorSubject<Optional<Cough.Status>> = createDefault(None)

    private val coughStatuses : List<Cough.Status> = listOf(
        BETTER_AND_WORSE_THROUGH_DAY, SAME_OR_STEADILY_WORSE, WORSE_WHEN_OUTSIDE
    )

    private val statusesObservable: Observable<List<CoughStatusViewData>> = selectedStatus
        .map { selectedStatus ->
            coughStatuses.map { it.toViewData(isChecked = it == selectedStatus.toNullable()) }
        }

    val statuses: LiveData<List<CoughStatusViewData>> = statusesObservable.toLiveData()

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

    private fun Cough.Status.toViewData(isChecked: Boolean): CoughStatusViewData =
        CoughStatusViewData(name(), isChecked, this)

    private fun Cough.Status.name(): String = when (this) {
        BETTER_AND_WORSE_THROUGH_DAY ->  symptom_report_cough_status_option_better_worse_day
        WORSE_WHEN_OUTSIDE ->  symptom_report_cough_status_option_worse_outside
        SAME_OR_STEADILY_WORSE ->  symptom_report_cough_status_option_same_or_steadily_worse
    }.let { resources.getString(it) }

    fun onSelected(item: CoughStatusViewData) {
        symptomFlowManager.setCoughStatus(item.status)
        symptomFlowManager.navigateForward()
    }
}
