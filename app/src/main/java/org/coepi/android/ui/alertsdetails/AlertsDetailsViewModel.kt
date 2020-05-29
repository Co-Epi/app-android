package org.coepi.android.ui.alertsdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import org.coepi.android.R.drawable.ic_alert
import org.coepi.android.domain.UnixTime
import org.coepi.android.tcn.Alert
import org.coepi.android.domain.symptomflow.SymptomId
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.formatters.DateFormatters

// TODO remove this screen? Appears not to be used
class AlertsDetailsViewModel(
    args: AlertsDetailsFragment.Args
) : ViewModel() {

    val report: LiveData<List<AlertDetailsSymptomViewData>> = just(args.report.toViewData())
        .toLiveData()

    val title: LiveData<String> = just(toMonthAndDay(args.report.contactTime))
        .toLiveData()

    val exposureTime: LiveData<String> = just(toTime(args.report.contactTime))
        .toLiveData()

    //TODO Show when the symptoms were reported. Maybe the earliestSymptom Date?
    val reportedTime: LiveData<String> = just(" ").toLiveData()

    private fun Alert.toViewData(): List<AlertDetailsSymptomViewData> =
        emptyList()
//        report.ids.map { it.toViewData() }

    private fun SymptomId.toViewData(): AlertDetailsSymptomViewData =
        // TODO map to localized names
        AlertDetailsSymptomViewData(ic_alert, name, this)

    companion object {
        fun toMonthAndDay(time: UnixTime ) =
            DateFormatters.monthDayFormatter.formatMonthDay(time.toDate())

        fun toTime(time: UnixTime) =
            DateFormatters.timeFormatter.formatTime(time.toDate())
    }
}


