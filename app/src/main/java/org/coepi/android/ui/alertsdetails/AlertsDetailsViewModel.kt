package org.coepi.android.ui.alertsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import org.coepi.android.R.drawable.ic_alert
import org.coepi.android.cen.ReceivedCenReport
import org.coepi.android.domain.model.Symptom
import org.coepi.android.extensions.rx.toLiveData

class AlertsDetailsViewModel(
    args: AlertsDetailsFragment.Args
) : ViewModel() {

    val alerts: LiveData<List<AlertDetailsSymptomViewData>> = just(args.report.toViewData())
        .toLiveData()

    // FIXME temporary title to identify the report. No design yet for this screen.
    val title: LiveData<String> = just("Report: ${args.report.report.id}")
        .toLiveData()

    private fun ReceivedCenReport.toViewData(): List<AlertDetailsSymptomViewData> =
        report.toSymptomsList().map { it.toViewData() }

    private fun Symptom.toViewData(): AlertDetailsSymptomViewData =
        AlertDetailsSymptomViewData(ic_alert, name, this)
}
