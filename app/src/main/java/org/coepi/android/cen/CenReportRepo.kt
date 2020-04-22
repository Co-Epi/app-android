package org.coepi.android.cen

import io.reactivex.Observable
import org.coepi.android.common.ApiSymptomsMapper
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.rx.VoidOperationState

interface CenReportRepo {
    val reports: Observable<List<SymptomReport>>

    val sendState: Observable<VoidOperationState>

    fun sendReport(report: SymptomReport)

    fun delete(report: SymptomReport)
}

class CenReportRepoImpl(
    private val cenReportDao: CenReportDao,
    private val coEpiRepo: CoEpiRepo,
    private val symptomsProcessor: ApiSymptomsMapper
) : CenReportRepo {
    override val reports: Observable<List<SymptomReport>> = cenReportDao.reports.map { reports ->
        reports.map {
            symptomsProcessor.fromCenReport(it.report)
        }
    }

    override val sendState: Observable<VoidOperationState> = coEpiRepo.sendReportState

    override fun sendReport(report: SymptomReport) {
        coEpiRepo.sendReport(report)
    }

    override fun delete(report: SymptomReport) {
        cenReportDao.delete(report)
    }
}
