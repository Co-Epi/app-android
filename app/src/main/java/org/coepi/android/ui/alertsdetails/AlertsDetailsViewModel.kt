package org.coepi.android.ui.alertsdetails

import android.annotation.SuppressLint
import android.app.Activity
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.PublishSubject
import org.coepi.android.R.string.alerts_details_distance_avg
import org.coepi.android.R.string.alerts_details_distance_unit_feet
import org.coepi.android.R.string.alerts_details_duration_hours_minutes
import org.coepi.android.R.string.alerts_details_duration_minutes
import org.coepi.android.R.string.alerts_details_duration_seconds
import org.coepi.android.R.string.alerts_details_report_email_address
import org.coepi.android.R.string.alerts_details_report_email_subject
import org.coepi.android.R.string.alerts_details_reported_on
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Email
import org.coepi.android.system.Resources
import org.coepi.android.system.log.log
import org.coepi.android.ui.extensions.ExposureDurationForUI
import org.coepi.android.ui.extensions.ExposureDurationForUI.HoursMinutes
import org.coepi.android.ui.extensions.ExposureDurationForUI.Minutes
import org.coepi.android.ui.extensions.ExposureDurationForUI.Seconds
import org.coepi.android.ui.extensions.durationForUI
import org.coepi.android.ui.extensions.symptomUIStrings
import org.coepi.android.ui.formatters.DateFormatters.hourMinuteFormatter
import org.coepi.android.ui.formatters.DateFormatters.monthDayFormatter
import org.coepi.android.ui.formatters.NumberFormatters.oneDecimal
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.core.domain.model.Alert
import org.coepi.core.domain.model.UnixTime

class AlertsDetailsViewModel(
    private val args: AlertsDetailsFragment.Args,
    private val resources: Resources,
    private val navigation: RootNavigation,
    private val alertsRepo: AlertsRepo,
    private val email: Email,
    private val nav: RootNavigation
) : ViewModel() {
    val viewData: AlertDetailsViewData = args.alert.toViewData()

    private val deleteAlertTrigger: PublishSubject<Unit> = PublishSubject.create()

    private val disposables = CompositeDisposable()

    init {
        disposables += deleteAlertTrigger
            .subscribeOn(io())
            .doOnNext { alertsRepo.removeAlert(args.alert) }
            .observeOn(mainThread())
            .subscribe { nav.navigate(Back) }
    }

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

    private fun Alert.toViewData(): AlertDetailsViewData = AlertDetailsViewData(
        title = toMonthAndDay(contactStart),
        contactStart = hourMinuteFormatter.formatTime(contactStart.toDate()),
        contactDuration = durationForUI.toLocalizedString(),
        avgDistance = resources.getString(
            alerts_details_distance_avg,
            oneDecimal.format(avgDistance.toFeet().value),
            resources.getString(alerts_details_distance_unit_feet)
        ),
        minDistance = "[DEBUG] Min distance: ${oneDecimal.format(minDistance.toFeet().value)} " +
                resources.getString(alerts_details_distance_unit_feet), // Temporary, for testing
        reportTime = reportedOnString(),
        symptoms = symptomList(this),
        alert = this
    ).also {
        log.d("Showing details for alert: $this")
    }

    @SuppressLint("DefaultLocale")
    private fun symptomList(alert: Alert): String = alert
        .symptomUIStrings(resources)
        .joinToString("\n")

    private fun toMonthAndDay(time: UnixTime): String =
        monthDayFormatter.formatMonthDay(time.toDate())

    fun onDeleteTap() {
        deleteAlertTrigger.onNext(Unit)
    }

    fun onReportProblemTap(activity: Activity) {
        email.send(activity,
            resources.getString(alerts_details_report_email_subject),
            resources.getString(alerts_details_report_email_address)
        )
    }

    private fun ExposureDurationForUI.toLocalizedString(): String = when (this) {
        is HoursMinutes -> resources.getString(alerts_details_duration_hours_minutes, hours, minutes)
        is Minutes -> resources.getString(alerts_details_duration_minutes, value)
        is Seconds -> resources.getString(alerts_details_duration_seconds, value)
    }
}

val Alert.contactDuration: Long get() =
    contactEnd.value - contactStart.value
