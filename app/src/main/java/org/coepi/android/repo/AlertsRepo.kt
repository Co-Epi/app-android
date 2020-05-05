package org.coepi.android.repo

import io.reactivex.Observable
import org.coepi.android.tcn.TcnReportRepo
import org.coepi.android.tcn.SymptomReport
import org.coepi.android.repo.reportsupdate.ReportsUpdater
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.VoidOperationState

interface AlertsRepo {
    val alerts: Observable<List<SymptomReport>>
    val updateReportsState: Observable<VoidOperationState>

    fun removeAlert(alert: SymptomReport)
    fun updateReports()
}

class AlertRepoImpl(
    private val tcnReportRepo: TcnReportRepo,
    private val reportsUpdater: ReportsUpdater
): AlertsRepo {

    override val alerts: Observable<List<SymptomReport>> = tcnReportRepo.reports

    override val updateReportsState: Observable<OperationState<Unit>> = reportsUpdater
        .updateState

    override fun removeAlert(alert: SymptomReport) {
        tcnReportRepo.delete(alert)
    }

    override fun updateReports() {
        reportsUpdater.requestUpdateReports()
    }
}
