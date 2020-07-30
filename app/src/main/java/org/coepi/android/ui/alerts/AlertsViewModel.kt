package org.coepi.android.ui.alerts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Resources
import org.coepi.android.system.log.log
import org.coepi.android.ui.alerts.AlertCellViewData.Header
import org.coepi.android.ui.alerts.AlertCellViewData.Item
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsDetails
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsInfo
import org.coepi.android.ui.alertsdetails.AlertsDetailsFragment.Args
import org.coepi.android.ui.extensions.symptomUIStrings
import org.coepi.android.ui.formatters.DateFormatters.hourMinuteFormatter
import org.coepi.android.ui.formatters.DateFormatters.monthDayFormatter
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation
import org.coepi.android.ui.notifications.NotificationsShower
import org.coepi.core.domain.model.Alert
import org.coepi.core.domain.common.Result.Failure
import org.coepi.core.domain.common.Result.Success

class AlertsViewModel(
    private val alertsRepo: AlertsRepo,
    private val resources: Resources,
    private val navigation: RootNavigation,
    private val notification: NotificationsShower
) : ViewModel() {

    val alerts: LiveData<List<AlertCellViewData>> = alertsRepo.alerts
        .map { it.toCellViewData(it) }
        .observeOn(mainThread())
        .toLiveData()

    private val selectAlertTrigger: PublishSubject<Alert> = create()

    private val disposables = CompositeDisposable()

    init {
        disposables += selectAlertTrigger.withLatestFrom(alertsRepo.alerts)
            .subscribeBy { (alert, alerts) ->
                when (val res = alertsRepo.updateIsRead(alert, true)) {
                    is Success ->
                        log.i("Alert: ${alert.id} was marked as read.")
                    is Failure ->
                        log.e("Alert: ${alert.id} couldn't be marked as read: ${res.error}")
                }
                navigation.navigate(
                    ToDirections(
                        actionGlobalAlertsDetails(
                            Args(alert, linkedAlerts(alert, alerts))
                        )
                    )
                )
            }
    }

    fun onAlertClick(alert: AlertViewData) {
        selectAlertTrigger.onNext(alert.alert)
    }

    fun onSwipeToRefresh() {
        alertsRepo.requestUpdateReports()
    }

    fun onBack() {
        navigation.navigate(Back)
    }

    fun onAlertsInfoButtonClick() {
        navigation.navigate(ToDirections(actionGlobalAlertsInfo()))
    }

    fun onAlertDismissed(alert: Alert) {
        alertsRepo.removeAlert(alert)
    }

    fun onUiReady() {
        if (notification.isShowingNotifications()) {
            notification.cancelAllNotifications()
        }
    }

    /**
     * This function parses [Alert] objects received from the [AlertsRepo] into readable strings that get displayed
     * in the recycler view item_alert views bound by the [AlertsAdapter]
     */
    private fun Alert.toViewData(allAlerts: List<Alert>): AlertViewData =
        AlertViewData(
            exposureType = symptomUIStrings(resources).joinToString(", "),
            contactTime = hourMinuteFormatter.formatTime(contactStart.toDate()),
            contactTimeMonth = monthDayFormatter.formatMonthDay(contactStart.toDate()),
            showUnreadDot = !isRead,
            showRepeatedInteraction = hasLinkedAlerts(this, allAlerts),
            alert = this
        )

    private fun List<Alert>.toCellViewData(allAlerts: List<Alert>): List<AlertCellViewData> =
        sortedWith(compareByDescending { it.contactStart.value })
            .groupBy { monthDayFormatter.formatMonthDay(it.contactStart.toDate()) }
            .flatMap { entry ->
                listOf(Header(entry.key)) + entry.value.map { alert ->
                    Item(alert.toViewData(allAlerts))
                }
            }

    private fun hasLinkedAlerts(alert: Alert, alerts: List<Alert>): Boolean =
        alerts.any { linkedAlertsPredicate(alert)(it) }

    private fun linkedAlerts(alert: Alert, alerts: List<Alert>): List<Alert> =
        alerts
            .filter { it.reportId == alert.reportId && it.id != alert.id }
            .sortedByDescending { it.contactStart.value }

    private fun linkedAlertsPredicate(alert: Alert): (Alert) -> Boolean =
        { it.reportId == alert.reportId && it.id != alert.id }
}
