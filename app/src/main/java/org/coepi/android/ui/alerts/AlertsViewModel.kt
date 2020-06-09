package org.coepi.android.ui.alerts

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.R.string.alerts_report_breathlessness
import org.coepi.android.R.string.alerts_report_cough
import org.coepi.android.R.string.alerts_report_cough_dry
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
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsInfo
import org.coepi.android.ui.alertsdetails.AlertsDetailsFragment.Args
import org.coepi.android.ui.formatters.DateFormatters
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation

class AlertsViewModel(
    val alertsRepo: AlertsRepo,
    private val resources: Resources,
    private val navigation: RootNavigation
) : ViewModel() {

    val alerts: LiveData<List<AlertViewData>> = alertsRepo.alerts
        .map { alerts ->
            alerts
                .sortedWith(compareByDescending { it.contactTime.value })
                .map { alert -> alert.toViewData(alerts) }
        }
        .observeOn(mainThread())
        .toLiveData()

    val updateStatusText: LiveData<String> = alertsRepo.updateReportsState
        .map { toUpdateStatusText(it) }
        .observeOn(mainThread())
        .toLiveData()

    fun onAlertClick(alert: AlertViewData) {
        navigation.navigate(ToDirections(actionGlobalAlertsDetails(Args(alert.report))))
    }

    fun onSwipeToRefresh() {
        alertsRepo.updateReports()
    }

    fun onBack() {
        navigation.navigate(Back)
    }

    fun onAlertsInfoButtonClick() {
        navigation.navigate(ToDirections(actionGlobalAlertsInfo()))
    }

    /**
     * This function parses [Alert] objects received from the [AlertsRepo] into readable strings that get displayed
     * in the recycler view item_alert views bound by the [AlertsAdapter]
     */
    private fun Alert.toViewData(alerts: List<Alert>): AlertViewData =
        AlertViewData(
            exposureType = listOfNotNull(
                report.coughSeverity.toUIString(),
                toBreathlessnessString(report.breathlessness),
                report.feverSeverity.toUIString()
            ).joinToString("\n"),
            contactTime = DateFormatters.hourMinuteFormatter.formatTime(contactTime.toDate()),
            contactTimeMonth = DateFormatters.monthDayFormatter.formatMonthDay(contactTime.toDate()),
            showMonthHeader = showMonthHeader(alerts),
            report = this
        )

    // TODO (low prio) date header should be a separate cell
    private fun Alert.showMonthHeader(alerts: List<Alert>): Boolean {
        val currentIndex = alerts.indexOf(this)
        val currentItemDate =
            DateFormatters.monthDayFormatter.formatMonthDay(this.contactTime.toDate())
        var previousItem: Alert? = null
        var monthHeaderVisible = true

        if (currentIndex > 0) {
            previousItem = alerts[currentIndex - 1]
        }

        previousItem?.let {
            monthHeaderVisible =
                currentItemDate != DateFormatters.monthDayFormatter.formatMonthDay(previousItem.contactTime.toDate())
        }

        return monthHeaderVisible
    }

    private fun toBreathlessnessString(breathless: Boolean): String? =
        if (breathless) resources.getString(alerts_report_breathlessness) else null

    private fun FeverSeverity.toUIString(): String? =
        when (this) {
            FeverSeverity.NONE -> null
            MILD -> resources.getString(alerts_report_fever_mild)
            SERIOUS -> resources.getString(alerts_report_fever_serious)
        }

    @SuppressLint("DefaultLocale")
    private fun CoughSeverity.toUIString(): String? =
        when (this) {
            CoughSeverity.NONE -> null
            EXISTING -> resources.getString(alerts_report_cough)
            WET -> resources.getString(alerts_report_cough_wet)
            DRY -> resources.getString(alerts_report_cough_dry)
        }

    private fun toUpdateStatusText(operationState: VoidOperationState): String =
        when (operationState) {
            is NotStarted, is Success -> ""
            is Progress -> "Updating..."
            is Failure -> "Error updating: ${operationState.t}"
        }
}
