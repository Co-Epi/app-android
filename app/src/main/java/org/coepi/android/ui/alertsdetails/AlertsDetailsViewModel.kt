package org.coepi.android.ui.alertsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import org.coepi.android.R.drawable.ic_alert
import org.coepi.android.tcn.Alert
import org.coepi.android.domain.symptomflow.SymptomId
import org.coepi.android.extensions.rx.toLiveData

// TODO remove this screen? Appears not to be used
class AlertsDetailsViewModel(
    args: AlertsDetailsFragment.Args
) : ViewModel() {

    val report: LiveData<List<AlertDetailsSymptomViewData>> = just(args.report.toViewData())
        .toLiveData()

    val title: LiveData<String> = just(args.report.contactTime.toMonthAndDay())
        .toLiveData()

    val reportedTime: LiveData<String> = just("Reported on ${args.report.report.earliestSymptomTime
        .map { it.toTime() }} at ${args.report.report.earliestSymptomTime.map {it.toTime()}}")
        .toLiveData()

    private fun Alert.toViewData(): List<AlertDetailsSymptomViewData> =
        emptyList()
//        report.ids.map { it.toViewData() }

    private fun SymptomId.toViewData(): AlertDetailsSymptomViewData =
        // TODO map to localized names
        AlertDetailsSymptomViewData(ic_alert, name, this)
}
