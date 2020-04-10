package org.coepi.android.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.R.plurals.alerts_new_notifications_count
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.Resources
import org.coepi.android.system.log.log
import org.coepi.android.ui.common.UINotificationData
import org.coepi.android.ui.extensions.rx.filterFailure
import org.coepi.android.ui.extensions.rx.toNotification
import java.util.Date

class AlertsViewModel(
    private val alertsRepo: AlertsRepo,
    private val resources: Resources,
    coEpiRepo: CoEpiRepo
) : ViewModel() {

    val errorNotification: LiveData<UINotificationData> = coEpiRepo.reports
        .toNotification()
        .filterFailure()
        .observeOn(mainThread())
        .toLiveData()

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
