package org.coepi.android.repo

import io.reactivex.Observable
import org.coepi.android.cen.CenReportRepo
import org.coepi.android.cen.ReceivedCenReport

interface AlertsRepo {
    val alerts: Observable<List<ReceivedCenReport>>

    fun removeAlert(alert: ReceivedCenReport)
}

class AlertRepoImpl(
    private val cenReportRepo: CenReportRepo
): AlertsRepo {

    override val alerts: Observable<List<ReceivedCenReport>> = cenReportRepo.reports

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

    override fun removeAlert(alert: ReceivedCenReport) {
        cenReportRepo.delete(alert)
    }
}
