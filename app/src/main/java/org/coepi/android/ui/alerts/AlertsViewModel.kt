package org.coepi.android.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import org.coepi.android.R.plurals.alerts_new_notifications_count
import org.coepi.android.cen.CenReport
import org.coepi.android.extensions.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Resources
import org.coepi.android.system.log.log
import org.coepi.android.ui.formatters.DateFormatters.dotFormatter

class AlertsViewModel(
    notificationsRepo: AlertsRepo,
    private val resources: Resources
) : ViewModel() {

    private val alertsObservable: Observable<List<AlertViewData>> = notificationsRepo.alerts()
        .map { reports -> reports.map { it.toViewData() } }
        .toObservable()

    val alerts: LiveData<List<AlertViewData>> = alertsObservable.toLiveData()

    val title = alertsObservable
        .map { title(it.size) }
        .startWith(title(0))
        .toLiveData()


    private fun title(alertsSize: Int) =
        resources.getQuantityString(alerts_new_notifications_count, alertsSize)

    private fun CenReport.toViewData(): AlertViewData =
        AlertViewData(report, dotFormatter.format(date), this) // TODO which date format?

    fun onAlertClick(viewData: AlertViewData) {
        log.i("Alert click: $viewData")
        // TODO
    }
}
