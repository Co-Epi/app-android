package org.coepi.android.ui.extensions

import org.coepi.android.R.string.alerts_report_breathlessness
import org.coepi.android.R.string.alerts_report_cough
import org.coepi.android.R.string.alerts_report_cough_dry
import org.coepi.android.R.string.alerts_report_cough_wet
import org.coepi.android.R.string.alerts_report_diarrhea
import org.coepi.android.R.string.alerts_report_fever
import org.coepi.android.R.string.alerts_report_loss_smell_or_taste
import org.coepi.android.R.string.alerts_report_muscle_aches
import org.coepi.android.R.string.alerts_report_no_symptoms
import org.coepi.android.R.string.alerts_report_other
import org.coepi.android.R.string.alerts_report_runny_nose
import org.coepi.android.system.Resources
import org.coepi.core.domain.model.Alert
import org.coepi.core.domain.model.CoughSeverity
import org.coepi.core.domain.model.CoughSeverity.DRY
import org.coepi.core.domain.model.CoughSeverity.EXISTING
import org.coepi.core.domain.model.CoughSeverity.WET
import org.coepi.core.domain.model.FeverSeverity
import org.coepi.core.domain.model.FeverSeverity.MILD
import org.coepi.core.domain.model.FeverSeverity.SERIOUS

fun Alert.symptomUIStrings(resources: Resources): List<String> =
    listOfNotNull(
        coughSeverity.toUIString(resources),
        if (breathlessness) resources.getString(alerts_report_breathlessness) else null,
        feverSeverity.toUIString(resources),
        if (muscleAches) resources.getString(alerts_report_muscle_aches) else null,
        if (lossSmellOrTaste) resources.getString(alerts_report_loss_smell_or_taste) else null,
        if (diarrhea) resources.getString(alerts_report_diarrhea) else null,
        if (runnyNose) resources.getString(alerts_report_runny_nose) else null,
        if (other) resources.getString(alerts_report_other) else null,
        if (noSymptoms) resources.getString(alerts_report_no_symptoms) else null
    )

fun FeverSeverity.toUIString(resources: Resources): String? =
    when (this) {
        FeverSeverity.NONE -> null
        MILD -> resources.getString(alerts_report_fever)
        SERIOUS -> resources.getString(alerts_report_fever)
    }

fun CoughSeverity.toUIString(resources: Resources): String? =
    when (this) {
        CoughSeverity.NONE -> null
        EXISTING -> resources.getString(alerts_report_cough)
        WET -> resources.getString(alerts_report_cough_wet)
        DRY -> resources.getString(alerts_report_cough_dry)
    }
