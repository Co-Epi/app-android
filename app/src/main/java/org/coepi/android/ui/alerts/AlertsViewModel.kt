package org.coepi.android.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import org.coepi.android.R.plurals.alerts_new_notifications_count
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Resources
import java.util.Date

class AlertsViewModel(
    private val alertsRepo: AlertsRepo,
    private val resources: Resources
) : ViewModel() {

    val alerts: LiveData<List<AlertViewData>> = alertsRepo.alerts
        .map { reports -> reports.map { it.toViewData() } }
        .toLiveData()

    val title = alertsRepo.alerts
        .map { title(it.size) }
        .startWith(title(0))
        .toLiveData()

    private fun title(alertsSize: Int) =
        resources.getQuantityString(alerts_new_notifications_count, alertsSize)

    private fun ReceivedCenReport.toViewData(): AlertViewData =
        AlertViewData(report.report, Date(report.timestamp).toString(), this) // TODO which date format?

    fun onAlertAckClick(alert: AlertViewData) {
        alertsRepo.removeAlert(alert.report)
    }
}
