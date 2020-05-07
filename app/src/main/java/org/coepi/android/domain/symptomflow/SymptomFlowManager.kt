package org.coepi.android.domain.symptomflow

import org.coepi.android.system.log.log
import org.coepi.android.ui.navigation.NavigationCommand.ToDestination
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.thanks.ThanksFragmentDirections.Companion.actionGlobalThanksFragment

class SymptomFlowManager(
    private val symptomRouter: SymptomRouter,
    private val rootNavigation: RootNavigation,
    private val symptomInputsManager: SymptomInputsManager
) : SymptomInputsProps by symptomInputsManager {

    private var symptomFlow: SymptomFlow? = null

    fun startFlow(symptomIds: List<SymptomId>): Boolean {
        if (symptomIds.isEmpty()) {
            log.w("Symptoms ids is empty")
            return false
        }
        symptomInputsManager.selectSymptomIds(symptomIds)
        val symptomFlow =
            SymptomFlow(symptomIds)
        this.symptomFlow = symptomFlow

        rootNavigation.navigate(ToDestination(symptomRouter.destination(symptomFlow.firstStep)))
        return true
    }

    fun navigateForward() {
        val symptomFlow = symptomFlow ?: error("Symptom flow not set")
        symptomFlow.next()?.let {
            rootNavigation.navigate(ToDestination(symptomRouter.destination(it)))
        } ?: {
            rootNavigation.navigate(ToDestination(actionGlobalThanksFragment())).also {
                clear()
            }
        }()
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
