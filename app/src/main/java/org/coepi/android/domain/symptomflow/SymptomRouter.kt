package org.coepi.android.domain.symptomflow

import androidx.navigation.NavDirections
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalCoughDurationFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalCoughStatusFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalCoughTypeFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalBreathlessFragment
import org.coepi.android.NavGraphRootDirections.Companion.actionGlobalDebug
import org.coepi.android.domain.symptomflow.SymptomStep.BREATHLESSNESS_DESCRIPTION
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_DAYS
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_DESCRIPTION
import org.coepi.android.domain.symptomflow.SymptomStep.COUGH_TYPE
import org.coepi.android.domain.symptomflow.SymptomStep.FEVER_DAYS
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
            COUGH_DAYS -> actionGlobalCoughDurationFragment()
            COUGH_DESCRIPTION -> actionGlobalCoughStatusFragment()
            BREATHLESSNESS_DESCRIPTION -> actionGlobalBreathlessFragment()

            // TODO new destinations
            FEVER_DAYS -> actionGlobalDebug()
            FEVER_TEMPERATURE_TAKEN_TODAY -> actionGlobalDebug()
            FEVER_TEMPERATURE_SPOT -> actionGlobalDebug()
            FEVER_TEMPERATURE_SPOT_INPUT -> actionGlobalDebug()
            FEVER_HIGHEST_TEMPERATURE -> actionGlobalDebug()
        }
}
