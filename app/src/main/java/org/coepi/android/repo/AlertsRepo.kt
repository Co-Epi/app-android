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
//    override fun alerts(): Single<List<CenReport>> = Single.just(listOf(
//        CenReport(1, "Report text1", "keys", "mime type", Date(), true),
//        CenReport(2, "Report text2", "keys", "mime type", Date(), true),
//        CenReport(3, "Report text3", "keys", "mime type", Date(), true),
//        CenReport(4, "Report text4", "keys", "mime type", Date(), true),
//        CenReport(5, "Report text5", "keys", "mime type", Date(), true),
//        CenReport(6, "Report text6", "keys", "mime type", Date(), true),
//        CenReport(7, "Report text7", "keys", "mime type", Date(), true)
//    ))

    override fun removeAlert(alert: ReceivedCenReport) {
        cenReportRepo.delete(alert)
    }
}
