package org.coepi.android.ui.extensions

import org.coepi.android.R.string
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.CoughSeverity.DRY
import org.coepi.android.api.publicreport.CoughSeverity.EXISTING
import org.coepi.android.api.publicreport.CoughSeverity.NONE
import org.coepi.android.api.publicreport.CoughSeverity.WET
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.FeverSeverity.MILD
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.system.Resources

fun PublicReport.AlertDetailToUIString(resources: Resources) : String {

    return listOfNotNull(
        this.coughSeverity.toUIString(resources),
        toBreathlessnessString(this.breathlessness, resources),
        this.feverSeverity.toUIString(resources)
    ).joinToString("\n")
}

private fun FeverSeverity.toUIString(resources: Resources): String? =
    when (this) {
        FeverSeverity.NONE -> null

        MILD -> resources.getString(string.bullet_point) + resources.getString(
            string.alerts_fever_report_detail,
            resources.getString(string.symptom_report_fever_severity_mild))

        SERIOUS -> resources.getString(string.bullet_point) + resources.getString(
                string.alerts_fever_report_detail,
                resources.getString(string.symptom_report_fever_severity_serious))
    }

private fun CoughSeverity.toUIString(resources: Resources): String? =
    when (this) {
        CoughSeverity.NONE -> null
        EXISTING -> resources.getString(string.bullet_point) +
                resources.getString(string.alerts_cough_report)

        WET -> resources.getString(string.bullet_point) + resources.getString(
            string.alerts_cough_report_detail,
            resources.getString(string.symptom_report_cough_type_wet)
        )
        DRY -> resources.getString(string.bullet_point) + resources.getString(
            string.alerts_cough_report_detail,
            resources.getString(string.symptom_report_cough_type_dry)
        )
    }

private fun toBreathlessnessString(breathless: Boolean, resources: Resources): String? =
    if (breathless) resources.getString(string.bullet_point) +
            resources.getString(string.alerts_breathlessness_report)
    else null