package org.coepi.android.domain.symptomflow

import androidx.navigation.NavDirections
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalBreathlessFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalCoughStatusFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalCoughTypeFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalEarliestSymptomFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalFeverHighestTemperatureFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalFeverTakenTodayFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalFeverTemperatureSpotFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalFeverTemperatureSpotInputFragment
import org.coepi.android.domain.symptomflow.SymptomStep.BREATHLESSNESS_DESCRIPTION
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_DESCRIPTION
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_TYPE
import org.coepi.android.domain.symptomflow.SymptomStep.EARLIEST_SYMPTOM
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_HIGHEST_TEMPERATURE
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_TEMPERATURE_SPOT
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_TEMPERATURE_SPOT_INPUT
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_TEMPERATURE_TAKEN_TODAY

interface SymptomRouter {
    // TODO abstract NavDirections (create NavDirections in UI), to remove Android specifics from domain
    fun destination(step: SymptomStep): NavDirections
}

class SymptomRouterImpl : SymptomRouter {
    override fun destination(step: SymptomStep): NavDirections =
        when (step) {
            COUGH_TYPE -> actionGlobalCoughTypeFragment()
            COUGH_DESCRIPTION -> actionGlobalCoughStatusFragment()
            BREATHLESSNESS_DESCRIPTION -> actionGlobalBreathlessFragment()
            FEVER_TEMPERATURE_TAKEN_TODAY -> actionGlobalFeverTakenTodayFragment()
            FEVER_HIGHEST_TEMPERATURE -> actionGlobalFeverHighestTemperatureFragment()
            FEVER_TEMPERATURE_SPOT -> actionGlobalFeverTemperatureSpotFragment()
            FEVER_TEMPERATURE_SPOT_INPUT -> actionGlobalFeverTemperatureSpotInputFragment()
            EARLIEST_SYMPTOM -> actionGlobalEarliestSymptomFragment()
        }
}
