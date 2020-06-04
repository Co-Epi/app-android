package org.coepi.android.ui.extensions

import org.coepi.android.R.string.alerts_report_breathlessness
import org.coepi.android.R.string.alerts_report_cough_dry
import org.coepi.android.R.string.alerts_report_cough
import org.coepi.android.R.string.alerts_report_cough_wet
import org.coepi.android.R.string.alerts_report_fever_mild
import org.coepi.android.R.string.alerts_report_fever_serious
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.CoughSeverity.DRY
import org.coepi.android.api.publicreport.CoughSeverity.EXISTING
import org.coepi.android.api.publicreport.CoughSeverity.WET
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.FeverSeverity.MILD
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.system.Resources

fun FeverSeverity.toUIString(resources: Resources): String? =
    when (this) {
        FeverSeverity.NONE -> null
        MILD -> resources.getString(alerts_report_fever_mild)
        SERIOUS -> resources.getString(alerts_report_fever_serious)
    }

fun CoughSeverity.toUIString(resources: Resources): String? =
    when (this) {
        CoughSeverity.NONE -> null
        EXISTING -> resources.getString(alerts_report_cough)
        WET -> resources.getString(alerts_report_cough_wet)
        DRY -> resources.getString(alerts_report_cough_dry)
    }

fun PublicReport.breathlessnessUIString(resources: Resources): String? =
    if (breathlessness)
        resources.getString(alerts_report_breathlessness)
    else null
