package org.coepi.android.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.R.plurals.alerts_new_notifications_count
import org.coepi.android.tcn.SymptomReport
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Resources
import org.coepi.android.system.rx.OperationState.Failure
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationState.Success
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsDetails
import org.coepi.android.ui.alertsdetails.AlertsDetailsFragment.Args
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation

class AlertsViewModel(
    private val alertsRepo: AlertsRepo,
    private val resources: Resources,
    private val navigation: RootNavigation
) : ViewModel() {

    val alerts: LiveData<List<AlertViewData>> = alertsRepo.alerts
        .map { reports -> reports.map { it.toViewData() } }
        .observeOn(mainThread())
        .toLiveData()

    val title: LiveData<String> = alertsRepo.alerts
        .map { title(it.size) }
        .startWith(title(0))
        .observeOn(mainThread())
        .toLiveData()

    val updateStatusText: LiveData<String> = alertsRepo.updateReportsState
        .map { toUpdateStatusText(it) }
        .observeOn(mainThread())
        .toLiveData()

    private fun title(alertsSize: Int) =
        resources.getQuantityString(alerts_new_notifications_count, alertsSize)

    private fun SymptomReport.toViewData(): AlertViewData =
        AlertViewData(
            exposureType = inputs.ids.joinToString(", ") { it.name }, // TODO map to localized names
            time = timestamp.toDate().toString(), // TODO which date format?
            report = this
        )

    fun onAlertAckClick(alert: AlertViewData) {
        alertsRepo.removeAlert(alert.report)
    }

    fun onAlertClick(alert: AlertViewData) {
        navigation.navigate(ToDirections(actionGlobalAlertsDetails(Args(alert.report))))
    }

    fun onSwipeToRefresh() {
        alertsRepo.updateReports()
    }

    private fun toUpdateStatusText(operationState: VoidOperationState): String =
        when (operationState) {
            is NotStarted, is Success -> ""
            is Progress -> "Updating..."
            is Failure -> "Error updating: ${operationState.t}"
    }
}
