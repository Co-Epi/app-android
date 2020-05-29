package org.coepi.android.ui.alertsdetails

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import org.coepi.android.R.drawable.ic_alert
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.CoughSeverity.DRY
import org.coepi.android.api.publicreport.CoughSeverity.EXISTING
import org.coepi.android.api.publicreport.CoughSeverity.WET
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.FeverSeverity.MILD
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomId
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.ui.formatters.DateFormatters

class AlertsDetailsViewModel(
    args: AlertsDetailsFragment.Args
) : ViewModel() {

    val title: LiveData<String> = just(toMonthAndDay(args.report.contactTime))
        .toLiveData()

    val exposureTime: LiveData<String> = just(toHourMinute(args.report.contactTime))
        .toLiveData()

    //TODO Show when the symptoms were reported. Maybe the earliestSymptom Date?
    val reportedTime: LiveData<String> = just(" ").toLiveData()

    val symptomList = symptomList(args.report.report)

    @SuppressLint("DefaultLocale")
    private fun symptomList(report: PublicReport): String {
        val cough = when (report.coughSeverity) {
            CoughSeverity.NONE -> ""
            EXISTING -> "• Cough" + "\n"
            WET -> "• Wet Cough" + "\n"
            DRY -> "• Dry Cough" + "\n"

        }
        val breathless =
            if (report.breathlessness) "• Breathless" + "\n" else ""

        val fever = when (report.feverSeverity) {
            FeverSeverity.NONE -> ""
            MILD -> "• Mild Fever" + "\n"
            SERIOUS -> "• Serious Fever" + "\n"
        }
        return cough + breathless + fever
    }

    private fun SymptomId.toViewData(): AlertDetailsSymptomViewData =
        // TODO map to localized names
        AlertDetailsSymptomViewData(ic_alert, name, this)

    private fun toMonthAndDay(time: UnixTime): String =
        DateFormatters.monthDayFormatter.formatMonthDay(time.toDate())

    private fun toHourMinute(time: UnixTime): String =
        DateFormatters.hourMinuteFormatter.formatTime(time.toDate())

}


