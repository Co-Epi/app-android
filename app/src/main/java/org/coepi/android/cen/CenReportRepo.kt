package org.coepi.android.cen

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.coepi.android.extensions.rx.success
import org.coepi.android.repo.CoEpiRepo
import org.coepi.android.system.log.log
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

    private val disposables = CompositeDisposable()

    init {
        disposables += coEpiRepo.reports.success().subscribeBy(onNext = {
            for (report in it) {
                cenReportDao.insert(report.report)
            }
        }, onError = {
            log.i("Error saving reports: $it")
        })
    }

    override fun sendReport(report: SymptomReport) {
        coEpiRepo.sendReport(report)
    }

    override fun delete(report: ReceivedCenReport) {
        cenReportDao.delete(report)
    }
}
