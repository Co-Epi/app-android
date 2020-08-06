package org.coepi.android.repo

import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers.io
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShower
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.core.domain.common.Result
import org.coepi.core.domain.common.Result.Failure
import org.coepi.core.domain.common.Result.Success
import org.coepi.core.domain.model.Alert
import org.coepi.core.services.AlertsApi

interface AlertsRepo {
    val alerts: Observable<List<Alert>>
    val alertsState: Observable<OperationState<List<Alert>>>

    fun removeAlert(alert: Alert): Result<Unit, Throwable>
    fun updateIsRead(alert: Alert, isRead: Boolean): Result<Unit, Throwable>

    fun requestUpdateReports()
}

class AlertRepoImpl(
    private val alertsApi: AlertsApi,
    private val newAlertsNotificationShower: NewAlertsNotificationShower,
    private val alertFilters: ObservableAlertFilters
): AlertsRepo {
    private val disposables = CompositeDisposable()

    override val alertsState: BehaviorSubject<OperationState<List<Alert>>> =
        createDefault(NotStarted)

    override val alerts: Observable<List<Alert>>

    private val removeAlertTrigger: PublishSubject<Alert> = create()
    private val updateIsReadTrigger: PublishSubject<AlertIsReadUpdatePars> = create()

    private val reportsUpdateTrigger: PublishSubject<Unit> = create()

    init {
        disposables += reportsUpdateTrigger
            .observeOn(io())
            .withLatestFrom(alertsState)
            .filter { (_, state) -> state !is Progress }
            .doOnNext { alertsState.onNext(Progress) }
            .subscribe {
                updateAlerts()
            }

        val alerts: Observable<List<Alert>> = alertsState.flatMap { state ->
            when (state) {
                is OperationState.Success -> just(state.data)
                else -> empty()
            }
        }
        this.alerts = Observables.combineLatest(alerts, alertFilters.filters) { alerts, filters ->
            filters.apply(alerts)
        }

        disposables += removeAlertTrigger
            .withLatestFrom(alerts)
            .subscribeBy { (alert, alerts) ->
                alertsState.onNext(OperationState.Success(alerts.minus(alert)))
            }

        disposables += updateIsReadTrigger
            .withLatestFrom(alerts)
            .subscribeBy { (pars, alerts) ->
                alertsState.onNext(OperationState.Success(alerts
                    .map { if (it == pars.alert) pars.alert.copy(isRead = pars.isRead) else it }
                ))
            }
    }

    override fun requestUpdateReports() {
        reportsUpdateTrigger.onNext(Unit)
    }

    private fun updateAlerts() {
        when (val result = alertsApi.fetchNewAlerts()) {
            is Success -> onFetchedAlertsSuccess(result.success)
            is Failure -> alertsState.onNext(OperationState.Failure(result.error))
        }
    }

    private fun onFetchedAlertsSuccess(alerts: List<Alert>) {
        alertsState.onNext(OperationState.Success(alerts))
        if (alerts.isNotEmpty()) {
            // TODO: figure out what we want these unique notifications ids to be - maybe associate
            //  them with alert ids
            newAlertsNotificationShower.showNotification(alerts.size, (0..1000000).random())
        }
    }

    override fun removeAlert(alert: Alert): Result<Unit, Throwable> {
        val result = alertsApi.deleteAlert(alert.id)
        when (result) {
            is Success ->
            // Note that alternatively we could return from Rust the updated alerts (from the local database)
            // but we're animating and we probably would have to perform this in the background
            removeAlertLocally(alert)
            is Failure -> {}
        }
        return result
    }

    override fun updateIsRead(alert: Alert, isRead: Boolean): Result<Unit, Throwable> {
        val result = alertsApi.updateIsRead(alert.id, isRead)
        when (result) {
            is Success -> updateIsReadLocally(alert, isRead)
            is Failure -> {}
        }
        return result
    }

    private fun removeAlertLocally(alert: Alert) {
        removeAlertTrigger.onNext(alert)
    }

    private fun updateIsReadLocally(alert: Alert, isRead: Boolean) {
        updateIsReadTrigger.onNext(AlertIsReadUpdatePars(alert, isRead))
    }
}

private data class AlertIsReadUpdatePars(val alert: Alert, val isRead: Boolean)
