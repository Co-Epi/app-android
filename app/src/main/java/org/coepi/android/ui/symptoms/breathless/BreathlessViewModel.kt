package org.coepi.android.ui.symptoms.breathless

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import org.coepi.android.R.drawable.ic_breathless_house
import org.coepi.android.R.drawable.ic_breathless_tired
import org.coepi.android.R.drawable.ic_breathless_ground
import org.coepi.android.R.drawable.ic_breathless_hill
import org.coepi.android.R.drawable.ic_breathless_exercise
import org.coepi.android.R.string.symptom_report_breathlessness_cause_option_exercise
import org.coepi.android.R.string.symptom_report_breathlessness_cause_option_ground_own_pace
import org.coepi.android.R.string.symptom_report_breathlessness_cause_option_hurry_or_hill
import org.coepi.android.R.string.symptom_report_breathlessness_cause_option_leaving_house_or_dressing
import org.coepi.android.R.string.symptom_report_breathlessness_cause_option_walking_yards_or_mins_on_ground
import org.coepi.android.domain.symptomflow.SymptomFlowManager
import org.coepi.android.extensions.rx.toIsInProgress
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.Resources
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.core.domain.common.Optional
import org.coepi.core.domain.common.Optional.None
import org.coepi.core.domain.common.toNullable
import org.coepi.core.domain.model.SymptomInputs.Breathlessness
import org.coepi.core.domain.model.SymptomInputs.Breathlessness.Cause.EXERCISE
import org.coepi.core.domain.model.SymptomInputs.Breathlessness.Cause.GROUND_OWN_PACE
import org.coepi.core.domain.model.SymptomInputs.Breathlessness.Cause.HURRY_OR_HILL
import org.coepi.core.domain.model.SymptomInputs.Breathlessness.Cause.LEAVING_HOUSE_OR_DRESSING
import org.coepi.core.domain.model.SymptomInputs.Breathlessness.Cause.WALKING_YARDS_OR_MINS_ON_GROUND
import org.coepi.core.domain.model.UserInput.Some

class BreathlessViewModel(
    private val navigation: RootNavigation,
    private val resources: Resources,
    private val symptomFlowManager: SymptomFlowManager
) : ViewModel() {

    val isInProgress: LiveData<Boolean> = symptomFlowManager.submitSymptomsState
        .toIsInProgress()
        .observeOn(mainThread())
        .toLiveData()

    private val selectedCause: BehaviorSubject<Optional<Breathlessness.Cause>> = createDefault(None)

    private val coughStatuses : List<Breathlessness.Cause> = listOf(
        LEAVING_HOUSE_OR_DRESSING, WALKING_YARDS_OR_MINS_ON_GROUND, GROUND_OWN_PACE, HURRY_OR_HILL, EXERCISE
    )

    private val causesObservable: Observable<List<BreathlessViewData>> = selectedCause
        .map { selectedCause ->
            coughStatuses.map { it.toViewData(isChecked = it == selectedCause.toNullable()) }
        }

    val causes: LiveData<List<BreathlessViewData>> = causesObservable.toLiveData()

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

    private fun Breathlessness.Cause.toViewData(isChecked: Boolean): BreathlessViewData =
        BreathlessViewData(name(), icon(), isChecked, this)

    private fun Breathlessness.Cause.name(): String = when (this) {
        LEAVING_HOUSE_OR_DRESSING ->  symptom_report_breathlessness_cause_option_leaving_house_or_dressing
        WALKING_YARDS_OR_MINS_ON_GROUND ->  symptom_report_breathlessness_cause_option_walking_yards_or_mins_on_ground
        GROUND_OWN_PACE ->  symptom_report_breathlessness_cause_option_ground_own_pace
        HURRY_OR_HILL ->  symptom_report_breathlessness_cause_option_hurry_or_hill
        EXERCISE ->  symptom_report_breathlessness_cause_option_exercise
    }.let { resources.getString(it) }

    private fun Breathlessness.Cause.icon(): Drawable? = when (this) {
        LEAVING_HOUSE_OR_DRESSING ->  ic_breathless_house
        WALKING_YARDS_OR_MINS_ON_GROUND ->  ic_breathless_tired
        GROUND_OWN_PACE ->  ic_breathless_ground
        HURRY_OR_HILL ->  ic_breathless_hill
        EXERCISE ->  ic_breathless_exercise
    }.let { resources.getDrawable(it) }

    fun onSelected(item: BreathlessViewData) {
        symptomFlowManager.setBreathlessnessCause(Some(item.breathless))
        symptomFlowManager.navigateForward()
    }
}
