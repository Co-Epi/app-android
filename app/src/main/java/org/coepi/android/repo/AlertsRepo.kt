package org.coepi.android.repo

import io.reactivex.Observable
import org.coepi.android.cen.CenReportRepo
import org.coepi.android.cen.SymptomReport
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
    private val cenReportRepo: CenReportRepo,
    private val reportsUpdater: ReportsUpdater
): AlertsRepo {

    override val alerts: Observable<List<SymptomReport>> = cenReportRepo.reports

    override val updateReportsState: Observable<OperationState<Unit>> = reportsUpdater
        .updateState

    // Dummy test data
//    override val alerts: Observable<List<ReceivedCenReport>> = just(listOf(
//        CenReport("1", "Report text1", 0),
//        CenReport("2", "Report text2", 0),
//        CenReport("3", "Report text3", 0),
//        CenReport("4", "Report text4", 0),
//        CenReport("5", "Report text5", 0),
//        CenReport("6", "Report text6", 0),
//        CenReport("7", "Report text7", 0)
//    ).map { ReceivedCenReport(it) })

    override fun removeAlert(alert: SymptomReport) {
        cenReportRepo.delete(alert)
    }

    override fun updateReports() {
        reportsUpdater.requestUpdateReports()
    }
}
