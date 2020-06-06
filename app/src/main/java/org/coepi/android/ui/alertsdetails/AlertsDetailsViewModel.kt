package org.coepi.android.ui.alertsdetails

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import org.coepi.android.R.drawable.ic_alert
import org.coepi.android.R.string
import org.coepi.android.R.string.alerts_details_reported_on
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomId
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.Resources
import org.coepi.android.ui.extensions.breathlessnessUIString
import org.coepi.android.ui.extensions.toUIString
import org.coepi.android.ui.formatters.DateFormatters.hourMinuteFormatter
import org.coepi.android.ui.formatters.DateFormatters.monthDayFormatter
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation

class AlertsDetailsViewModel(
    args: AlertsDetailsFragment.Args,
    private val resources: Resources,
    private val navigation: RootNavigation
) : ViewModel() {

    val title: LiveData<String> = just(toMonthAndDay(args.report.contactTime))
        .toLiveData()

    val exposureTime: LiveData<String> = just(toHourMinute(args.report.contactTime))
        .toLiveData()

    val reportedTime: LiveData<String> =
        just(args.alert.reportedOnString())
        .toLiveData()

    val symptomList = symptomList(args.report.report)

    @SuppressLint("DefaultLocale")
    private fun symptomList(report: PublicReport): String =
        report.toUIString(resources)

    private fun SymptomId.toViewData(): AlertDetailsSymptomViewData =
        // TODO map to localized names
        AlertDetailsSymptomViewData(ic_alert, name, this)

    private fun toMonthAndDay(time: UnixTime): String =
        monthDayFormatter.formatMonthDay(time.toDate())

    private fun toHourMinute(time: UnixTime): String =
        hourMinuteFormatter.formatTime(time.toDate())

    private fun PublicReport.toUIString(resources: Resources): String =
        listOfNotNull(
            coughSeverity.toUIString(resources),
            breathlessnessUIString(resources),
            feverSeverity.toUIString(resources)
        ).joinToString("\n") { "${resources.getString(string.bullet_point)} $it" }

    fun onBack() {
        navigation.navigate(Back)
    }

    private fun Alert.reportedOnString() = report.reportTime.toDate().let { date ->
        resources.getString(alerts_details_reported_on,
            monthDayFormatter.formatMonthDay(date),
            hourMinuteFormatter.formatTime(date)
        )
    }
}
