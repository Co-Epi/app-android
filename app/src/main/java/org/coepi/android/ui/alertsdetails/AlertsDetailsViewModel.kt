package org.coepi.android.ui.alertsdetails

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable.just
import org.coepi.android.R.string
import org.coepi.android.R.string.alerts_details_reported_on
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.system.Resources
import org.coepi.android.ui.extensions.symptomUIStrings
import org.coepi.android.ui.formatters.DateFormatters.hourMinuteFormatter
import org.coepi.android.ui.formatters.DateFormatters.monthDayFormatter
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.core.domain.model.Alert
import org.coepi.core.domain.model.UnixTime

class AlertsDetailsViewModel(
    args: AlertsDetailsFragment.Args,
    private val resources: Resources,
    private val navigation: RootNavigation
) : ViewModel() {

    val title: LiveData<String> = just(toMonthAndDay(args.alert.contactTime))
        .toLiveData()

    val exposureTime: LiveData<String> = just(toHourMinute(args.alert.contactTime))
        .toLiveData()

    val reportedTime: LiveData<String> =
        just(args.alert.reportedOnString())
        .toLiveData()

    val symptomList = symptomList(args.alert)

    @SuppressLint("DefaultLocale")
    private fun symptomList(alert: Alert): String = alert
        .symptomUIStrings(resources)
        .joinToString("\n") { symptom -> "${resources.getString(string.bullet_point)} $symptom" }

    private fun toMonthAndDay(time: UnixTime): String =
        monthDayFormatter.formatMonthDay(time.toDate())

    private fun toHourMinute(time: UnixTime): String =
        hourMinuteFormatter.formatTime(time.toDate())

    fun onBack() {
        navigation.navigate(Back)
    }

    private fun Alert.reportedOnString() = reportTime.toDate().let { date ->
        resources.getString(
            alerts_details_reported_on,
            monthDayFormatter.formatMonthDay(date),
            hourMinuteFormatter.formatTime(date)
        )
    }
}
