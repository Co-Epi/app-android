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
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.Failure
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationState.Success
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

class AlertsViewModel(
    private val alertsRepo: AlertsRepo,
    private val resources: Resources,
    private val navigation: RootNavigation,
    private val notification: NotificationsShower
) : ViewModel() {

    val alerts: LiveData<List<AlertCellViewData>> = alertsRepo.alerts
        .map { it.toCellViewData() }
        .observeOn(mainThread())
        .toLiveData()

    val updateStatusText: LiveData<String> = alertsRepo.alertsState
        .map { toUpdateStatusText(it) }
        .observeOn(mainThread())
        .toLiveData()

    private val selectAlertTrigger: PublishSubject<Alert> = create()

    private val disposables = CompositeDisposable()

    init {
        disposables += selectAlertTrigger.withLatestFrom(alertsRepo.alerts)
            .map { (alert, alerts) ->
                Args(alert, linkedAlerts(alert, alerts))
            }
            .subscribeBy { args ->
                navigation.navigate(ToDirections(actionGlobalAlertsDetails(args)))
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
    private fun Alert.toViewData(): AlertViewData =
        AlertViewData(
            exposureType = symptomUIStrings(resources).joinToString("\n"),
            contactTime = hourMinuteFormatter.formatTime(contactStart.toDate()),
            contactTimeMonth = monthDayFormatter.formatMonthDay(contactStart.toDate()),
            alert = this
        )

    private fun List<Alert>.toCellViewData(): List<AlertCellViewData> =
        sortedWith(compareByDescending { it.contactStart.value })
            .groupBy { monthDayFormatter.formatMonthDay(it.contactStart.toDate()) }
            .flatMap { entry ->
                listOf(Header(entry.key)) + entry.value.map { alert ->
                    Item(alert.toViewData())
                }
            }

    private fun toUpdateStatusText(operationState: OperationState<List<Alert>>): String =
        when (operationState) {
            is NotStarted, is Success -> ""
            is Progress -> "Updating..."
            is Failure -> "Error updating: ${operationState.t}"
        }

    private fun linkedAlerts(alert: Alert, alerts: List<Alert>): List<Alert> =
        alerts
            .filter { it.reportId == alert.reportId && it.id != alert.id }
            .sortedByDescending { it.contactStart.value }
}
