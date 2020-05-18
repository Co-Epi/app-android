package org.coepi.android.repo

import io.reactivex.Observable
import org.coepi.android.tcn.TcnReportRepo
import org.coepi.android.tcn.Alert
import org.coepi.android.repo.reportsupdate.ReportsUpdater
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.VoidOperationState

interface AlertsRepo {
    val alerts: Observable<List<Alert>>
    val updateReportsState: Observable<VoidOperationState>

    fun removeAlert(alert: Alert)
    fun updateReports()
}

class AlertRepoImpl(
    private val tcnReportRepo: TcnReportRepo,
    private val reportsUpdater: ReportsUpdater
): AlertsRepo {

    override val alerts: Observable<List<Alert>> = tcnReportRepo.alerts

    override val updateReportsState: Observable<OperationState<Unit>> = reportsUpdater
        .updateState

    override fun removeAlert(alert: Alert) {
        tcnReportRepo.delete(alert)
    }

    override fun updateReports() {
        reportsUpdater.requestUpdateReports()
    }
}
