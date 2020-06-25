package org.coepi.android.domain.symptomflow

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.extensions.expect
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.ui.common.UINotificationData.Failure
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.thanks.ThanksFragmentDirections.Companion.actionGlobalThanksFragment
import org.coepi.core.domain.model.SymptomInputs.Breathlessness
import org.coepi.core.domain.model.SymptomInputs.Cough
import org.coepi.core.domain.model.SymptomInputs.Fever
import org.coepi.core.domain.model.Temperature
import org.coepi.core.domain.model.UserInput
import org.coepi.core.domain.common.Result
import org.coepi.core.domain.model.SymptomId
import org.coepi.core.services.SymptomsInputManager

interface SymptomFlowManager {
    val submitSymptomsState: Observable<VoidOperationState>

    fun setCoughType(type: UserInput<Cough.Type>): Result<Unit, Throwable>
    fun setCoughDays(days: UserInput<Cough.Days>): Result<Unit, Throwable>
    fun setCoughStatus(status: UserInput<Cough.Status>): Result<Unit, Throwable>
    fun setBreathlessnessCause(cause: UserInput<Breathlessness.Cause>): Result<Unit, Throwable>
    fun setFeverDays(days: UserInput<Fever.Days>): Result<Unit, Throwable>
    fun setFeverTakenTemperatureToday(taken: UserInput<Boolean>): Result<Unit, Throwable>
    fun setFeverTakenTemperatureSpot(spot: UserInput<Fever.TemperatureSpot>): Result<Unit, Throwable>
    fun setFeverHighestTemperatureTaken(temp: UserInput<Temperature>): Result<Unit, Throwable>
    fun setEarliestSymptomStartedDaysAgo(days: UserInput<Int>): Result<Unit, Throwable>

    fun startFlow(symptomIds: List<SymptomId>): Boolean

    fun removeIfPresent(step: SymptomStep)
    fun addUniqueStepAfterCurrent(step: SymptomStep)

    fun navigateForward()
    fun onBack()
}

class SymptomFlowManagerImpl(
    private val symptomRouter: SymptomRouter,
    private val rootNavigation: RootNavigation,
    private val inputsManager: SymptomsInputManager,
    private val uiNotifier: UINotifier
) : SymptomFlowManager {

    override val submitSymptomsState: PublishSubject<VoidOperationState> = create()

    private var symptomFlow: SymptomFlow? = null

    private val finishFlowTrigger: PublishSubject<Unit> = create()

    private val disposables = CompositeDisposable()

    init {
        disposables += finishFlowTrigger
            .observeOn(io())
            .doOnNext { submitSymptomsState.onNext(OperationState.Progress) }
            .map { inputsManager.submitSymptoms() }
            .observeOn(mainThread())
            .subscribe { handleSubmitReportResult(it) }
    }

    override fun startFlow(symptomIds: List<SymptomId>): Boolean {
        if (symptomIds.isEmpty()) {
            log.w("Symptoms ids is empty")
            return false
        }

        inputsManager.setSymptoms(symptomIds.toSet())
        symptomFlow = SymptomFlow.create(symptomIds)

        updateNavigation()
        return true
    }

    override fun navigateForward() {
        val symptomFlow = this.symptomFlow
        // Navigate forward is called from screens in the input flow
            ?: error("Can't navigate forward if there's no input flow")

        if (symptomFlow.next() == null) {
            finishFlowTrigger.onNext(Unit)
        } else {
            updateNavigation()
        }
    }

    override fun onBack() {
        val symptomFlow = symptomFlow ?: error("Symptom flow not set")
        if (symptomFlow.previous() == null) {
            log.d("No previous step.")
        }
    }

    private fun updateNavigation() {
        val symptomFlow = this.symptomFlow
        if (symptomFlow == null) {
            log.d("No symptom inputs. Showing thanks screen.")
            finishFlowTrigger.onNext(Unit)
            return
        }
        rootNavigation.navigate(ToDestination(symptomRouter.destination(symptomFlow.currentStep)))
    }

    private fun handleSubmitReportResult(result: Result<Unit, Throwable>) {
        when (result) {
            is Result.Success -> {
                submitSymptomsState.onNext(OperationState.Success(Unit))
                rootNavigation.navigate(ToDestination(actionGlobalThanksFragment()))
                clear()
            }
            is Result.Failure -> {
                submitSymptomsState.onNext(OperationState.Failure(result.error))
                // TODO user friendly / localized error message (and retry etc)
                uiNotifier.notify(Failure(result.error.message ?: "Unknown error"))
            }
        }
        submitSymptomsState.onNext(OperationState.NotStarted)
    }

    override fun removeIfPresent(step: SymptomStep) {
        symptomFlow?.removeIfPresent(step)
    }

    override fun addUniqueStepAfterCurrent(step: SymptomStep) {
        symptomFlow?.addUniqueStepAfterCurrent(step)
    }

    private fun clear() {
        symptomFlow = null
        inputsManager.clearSymptoms().expect()
    }

    override fun setCoughType(type: UserInput<Cough.Type>): Result<Unit, Throwable> =
        inputsManager.setCoughType(type)

    override fun setCoughDays(days: UserInput<Cough.Days>): Result<Unit, Throwable> =
        inputsManager.setCoughDays(days)

    override fun setCoughStatus(status: UserInput<Cough.Status>): Result<Unit, Throwable> =
        inputsManager.setCoughStatus(status)

    override fun setBreathlessnessCause(cause: UserInput<Breathlessness.Cause>): Result<Unit, Throwable> =
        inputsManager.setBreathlessnessCause(cause)

    override fun setFeverDays(days: UserInput<Fever.Days>): Result<Unit, Throwable> =
        inputsManager.setFeverDays(days)

    override fun setFeverTakenTemperatureToday(taken: UserInput<Boolean>): Result<Unit, Throwable> =
        inputsManager.setFeverTakenTemperatureToday(taken)

    override fun setFeverTakenTemperatureSpot(spot: UserInput<Fever.TemperatureSpot>): Result<Unit, Throwable> =
        inputsManager.setFeverTakenTemperatureSpot(spot)

    override fun setFeverHighestTemperatureTaken(temp: UserInput<Temperature>): Result<Unit, Throwable> =
        inputsManager.setFeverHighestTemperatureTaken(temp)

    override fun setEarliestSymptomStartedDaysAgo(days: UserInput<Int>): Result<Unit, Throwable> =
        inputsManager.setEarliestSymptomStartedDaysAgo(days)
}
