package org.coepi.android.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.R.plurals.alerts_new_notifications_count
import org.coepi.android.cen.CenReport
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.extensions.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Resources
import org.coepi.android.system.log.log
import org.coepi.android.ui.formatters.DateFormatters.dotFormatter
import java.util.Date

class AlertsViewModel(
    notificationsRepo: AlertsRepo,
    private val resources: Resources
) : ViewModel() {

    private val alertsObservable: Observable<List<AlertViewData>> = notificationsRepo.alerts
        .map { reports -> reports.map { it.toViewData() } }
        .observeOn(mainThread())

    val alerts: LiveData<List<AlertViewData>> = alertsObservable.toLiveData()

    val title = alertsObservable
        .map { title(it.size) }
        .startWith(title(0))
        .toLiveData()


    private fun title(alertsSize: Int) =
        resources.getQuantityString(alerts_new_notifications_count, alertsSize)

    private fun ReceivedCenReport.toViewData(): AlertViewData =
        AlertViewData(report.report, dotFormatter.format(Date(report.timestamp)), this.report) // TODO which date format?

    fun onAlertClick(viewData: AlertViewData) {
        log.i("Alert click: $viewData")
        // TODO
    }
}
