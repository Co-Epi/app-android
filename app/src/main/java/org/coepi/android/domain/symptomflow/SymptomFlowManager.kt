package org.coepi.android.domain.symptomflow

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.common.Result
import org.coepi.android.common.doIfError
import org.coepi.android.common.doIfSuccess
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.tcn.TcnReportRepo
import org.coepi.android.ui.common.UINotificationData.Failure
import org.coepi.android.ui.common.UINotifier
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.thanks.ThanksFragmentDirections.Companion.actionGlobalEarliestSymptomFragment
import org.coepi.android.ui.thanks.ThanksFragmentDirections.Companion.actionGlobalThanksFragment

class SymptomFlowManager(
    private val symptomRouter: SymptomRouter,
    private val rootNavigation: RootNavigation,
    private val symptomInputsManager: SymptomInputsManager,
    private val reportRepo: TcnReportRepo,
    private val uiNotifier: UINotifier
) : SymptomInputsProps by symptomInputsManager {

    private var symptomFlow: SymptomFlow? = null

    private val finishFlowTrigger: PublishSubject<SymptomInputs> = create()

    private val disposables = CompositeDisposable()

    val sendReportState: Observable<VoidOperationState> = reportRepo.sendState.share()

    init {
        disposables += finishFlowTrigger
            .observeOn(io())
            .map { reportRepo.submitReport(inputs) }
            .observeOn(mainThread())
            .subscribe { handleSubmitReportResult(it) }
    }

    fun startFlow(symptomIds: List<SymptomId>): Boolean {
        if (symptomIds.isEmpty()) {
            log.w("Symptoms ids is empty")
            return false
        }
        symptomInputsManager.selectSymptomIds(symptomIds.toSet())
        val symptomFlow = SymptomFlow(symptomIds)
        this.symptomFlow = symptomFlow

        rootNavigation.navigate(ToDestination(symptomRouter.destination(symptomFlow.firstStep)))
        return true
    }

    fun navigateForward() {
        val symptomFlow = symptomFlow ?: error("Symptom flow not set")
        symptomFlow.next()?.let {
            rootNavigation.navigate(ToDestination(symptomRouter.destination(it)))
        } ?: finishFlowTrigger.onNext(inputs)
    }

    private fun handleSubmitReportResult(result: Result<Unit, Throwable>) {
        result
            .doIfSuccess {

                rootNavigation.navigate(ToDestination(actionGlobalThanksFragment())).also {
                    clear()
                }
            }
            .doIfError {
                // TODO user friendly / localized error message (and retry etc)
                uiNotifier.notify(Failure(it.message ?: "Unknown error"))
            }
    }

    fun onBack() {
        val symptomFlow = symptomFlow ?: error("Symptom flow not set")
        symptomFlow.previous()
    }

    private fun clear() {
        symptomFlow = null
        symptomInputsManager.clear()
    }
}
