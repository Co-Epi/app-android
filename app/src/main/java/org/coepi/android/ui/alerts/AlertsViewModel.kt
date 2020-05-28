package org.coepi.android.ui.alerts

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.R.string.alerts_breathlessness_report
import org.coepi.android.R.string.alerts_cough_report
import org.coepi.android.R.string.alerts_fever_report
import org.coepi.android.R.string.symptom_report_cough_type_dry
import org.coepi.android.R.string.symptom_report_cough_type_wet
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.CoughSeverity.DRY
import org.coepi.android.api.publicreport.CoughSeverity.EXISTING
import org.coepi.android.api.publicreport.CoughSeverity.WET
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.FeverSeverity.MILD
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Resources
import org.coepi.android.system.rx.OperationState.Failure
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationState.Success
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.tcn.Alert
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsDetails
import org.coepi.android.ui.alertsdetails.AlertsDetailsFragment.Args
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation
import java.text.SimpleDateFormat

class AlertsViewModel(
    private val alertsRepo: AlertsRepo,
    private val resources: Resources,
    private val navigation: RootNavigation
) : ViewModel() {

    val alerts: LiveData<List<AlertViewData>> = alertsRepo.alerts
        .map { reports -> reports.map { it.toViewData() } }
        .observeOn(mainThread())
        .toLiveData()

    val updateStatusText: LiveData<String> = alertsRepo.updateReportsState
        .map { toUpdateStatusText(it) }
        .observeOn(mainThread())
        .toLiveData()

    private fun Alert.toViewData(): AlertViewData =
        AlertViewData(
            exposureType = report.toExposuresString(),
            contactTime = contactTime.toTimeString(),
            report = this
        )

    @SuppressLint("SimpleDateFormat")
    private fun UnixTime.toTimeString(): String {
        val sdf = SimpleDateFormat("h:mm a")
        return sdf.format(toDate())
    }

    @SuppressLint("DefaultLocale")
    private fun PublicReport.toExposuresString(): String {
        val cough = when (coughSeverity) {
            CoughSeverity.NONE -> ""
            EXISTING -> resources.getString(alerts_cough_report).capitalize() + "\n"
            WET -> resources.getString(symptom_report_cough_type_wet) + " " + resources.getString(
                alerts_cough_report
            ) + "\n"
            DRY -> resources.getString(symptom_report_cough_type_dry) + " " + resources.getString(
                alerts_cough_report
            ) + "\n"
        }

        val breathless =
            if (breathlessness) resources.getString(alerts_breathlessness_report) + "\n" else ""

        val fever = when (feverSeverity) {
            FeverSeverity.NONE -> ""
            else -> resources.getString(alerts_fever_report)
        }

        return cough + breathless + fever
    }

    fun onAlertClick(alert: AlertViewData) {
        navigation.navigate(ToDirections(actionGlobalAlertsDetails(Args(alert.report))))
    }

    fun onSwipeToRefresh() {
        alertsRepo.updateReports()
    }

    fun onBack() {
        navigation.navigate(Back)
    }

    fun testAlertData(): MutableList<AlertViewData> {

        val report1 = PublicReport(
            earliestSymptomTime = Some(UnixTime.fromValue(1589209754L)),
            feverSeverity = SERIOUS,
            breathlessness = true,
            coughSeverity = EXISTING
        )

        val report2 = PublicReport(
            earliestSymptomTime = Some(UnixTime.fromValue(1589209754L)),
            feverSeverity = MILD,
            breathlessness = false,
            coughSeverity = DRY
        )

        val report3 = PublicReport(
            earliestSymptomTime = Some(UnixTime.fromValue(1589209754L)),
            feverSeverity = SERIOUS,
            breathlessness = true,
            coughSeverity = WET
        )

        return listOf(
            Alert("id1", report1, UnixTime.fromValue(1589209754L)).toViewData(),
            Alert("id2", report2, UnixTime.fromValue(1589209754L)).toViewData(),
            Alert("id3", report3, UnixTime.fromValue(1589209754L)).toViewData()
        ).toMutableList()

    }

    private fun toUpdateStatusText(operationState: VoidOperationState): String =
        when (operationState) {
            is NotStarted, is Success -> ""
            is Progress -> "Updating..."
            is Failure -> "Error updating: ${operationState.t}"
        }
}
