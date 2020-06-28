package org.coepi.android.repo

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.coepi.core.domain.common.Result.Failure
import org.coepi.core.domain.common.Result.Success
import org.coepi.android.repo.reportsupdate.NewAlertsNotificationShower
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.tcn.AlertsDao
import org.coepi.core.domain.model.Alert
import org.coepi.core.services.AlertsFetcher

interface AlertsRepo {
    val alerts: Observable<List<Alert>>
    val updateReportsState: Observable<VoidOperationState>

    fun removeAlert(alert: Alert)

    fun requestUpdateReports()
}

class AlertRepoImpl(
    private val alertsDao: AlertsDao,
    private val alertsFetcher: AlertsFetcher,
    private val newAlertsNotificationShower: NewAlertsNotificationShower
): AlertsRepo {

    private val disposables = CompositeDisposable()

    override val updateReportsState: BehaviorSubject<VoidOperationState> =
        BehaviorSubject.createDefault(NotStarted)

    private val reportsUpdateTrigger: PublishSubject<Unit> = PublishSubject.create()

    override val alerts: Observable<List<Alert>> = alertsDao.alerts

    init {
        disposables += reportsUpdateTrigger
            .observeOn(Schedulers.io())
            .withLatestFrom(updateReportsState)
            .filter { (_, state) -> state !is Progress }
            .doOnNext { updateReportsState.onNext(Progress) }
            .subscribe {
                updateAlerts()
            }
    }

    override fun requestUpdateReports() {
        reportsUpdateTrigger.onNext(Unit)
    }

    private fun updateAlerts() {
        when (val result = alertsFetcher.fetchNewAlerts()) {
            is Success -> onFetchedAlertsSuccess(result.success)
            is Failure -> updateReportsState.onNext(OperationState.Failure(result.error))
        }
    }

    private fun onFetchedAlertsSuccess(alerts: List<Alert>) {
        val insertedCount = storeAlerts(alerts)
        if (insertedCount > 0) {
            // TODO: figure out what we want these unique notifications ids to be - maybe associate
            //  them with alert ids
            newAlertsNotificationShower.showNotification(insertedCount, (0..1000000).random())
        }
        updateReportsState.onNext(OperationState.Success(Unit))
    }

    /**
     * Stores alerts in the database
     * @return count of inserted alerts. This can differ from alerts count, if alerts
     * are already in the db.
     */
    private fun storeAlerts(alerts: List<Alert>): Int {
        val insertedCount: Int = alerts.map {
            alertsDao.insert(it)
        }.filter { it }.size

        if (insertedCount >= 0) {
            log.d("Added $insertedCount new alerts")
        }

        return insertedCount
    }

    override fun removeAlert(alert: Alert) {
        alertsDao.delete(alert)
    }
}
