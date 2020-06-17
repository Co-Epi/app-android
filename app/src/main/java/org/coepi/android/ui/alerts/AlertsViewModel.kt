package org.coepi.android.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Resources
import org.coepi.android.system.rx.OperationState.Failure
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationState.Success
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.tcn.Alert
import org.coepi.android.ui.alerts.AlertCellViewData.Header
import org.coepi.android.ui.alerts.AlertCellViewData.Item
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsDetails
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsInfo
import org.coepi.android.ui.alertsdetails.AlertsDetailsFragment.Args
import org.coepi.android.ui.extensions.symptomUIStrings
import org.coepi.android.ui.formatters.DateFormatters.hourMinuteFormatter
import org.coepi.android.ui.formatters.DateFormatters.monthDayFormatter
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation

class AlertsViewModel(
    val alertsRepo: AlertsRepo,
    private val resources: Resources,
    private val navigation: RootNavigation
) : ViewModel() {

    val alerts: LiveData<List<AlertCellViewData>> = alertsRepo.alerts
        .map { it.toCellViewData() }
        .observeOn(mainThread())
        .toLiveData()

    val updateStatusText: LiveData<String> = alertsRepo.updateReportsState
        .map { toUpdateStatusText(it) }
        .observeOn(mainThread())
        .toLiveData()

    fun onAlertClick(alert: AlertViewData) {
        navigation.navigate(ToDirections(actionGlobalAlertsDetails(Args(alert.alert))))
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
    private fun Alert.toViewData(): AlertViewData =
        AlertViewData(
            exposureType = report.symptomUIStrings(resources).joinToString("\n"),
            contactTime = hourMinuteFormatter.formatTime(contactTime.toDate()),
            contactTimeMonth = monthDayFormatter.formatMonthDay(contactTime.toDate()),
            alert = this
        )

    private fun List<Alert>.toCellViewData(): List<AlertCellViewData> =
        sortedWith(compareByDescending { it.contactTime.value })
            .groupBy { monthDayFormatter.formatMonthDay(it.contactTime.toDate()) }
            .flatMap { entry ->
                listOf(Header(entry.key)) + entry.value.map { alert ->
                    Item(alert.toViewData())
                }
            }

    private fun toUpdateStatusText(operationState: VoidOperationState): String =
        when (operationState) {
            is NotStarted, is Success -> ""
            is Progress -> "Updating..."
            is Failure -> "Error updating: ${operationState.t}"
        }
}
