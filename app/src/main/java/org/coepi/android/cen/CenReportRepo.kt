package org.coepi.android.cen

import io.reactivex.Observable
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.rx.VoidOperationState

interface CenReportRepo {
    val reports: Observable<List<ReceivedCenReport>>

    val sendState: Observable<VoidOperationState>

    fun sendReport(report: SymptomReport)

    fun delete(report: ReceivedCenReport)
}

class CenReportRepoImpl(
    private val cenReportDao: RealmCenReportDao,
    private val coEpiRepo: CoEpiRepo
) : CenReportRepo {
    override val reports: Observable<List<ReceivedCenReport>> = cenReportDao.reports

    override val sendState: Observable<VoidOperationState> = coEpiRepo.sendReportState

    override fun sendReport(report: SymptomReport) {
        coEpiRepo.sendReport(report)
    }

    override fun delete(report: ReceivedCenReport) {
        cenReportDao.delete(report)
    }
}
