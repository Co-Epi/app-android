package org.coepi.android.repo

import io.reactivex.Completable
import io.reactivex.Observable
import org.coepi.android.cen.Cen
import org.coepi.android.cen.CenRepo
import org.coepi.android.cen.CenReport
import org.coepi.android.cen.SymptomReport

interface CoEpiRepo {
    // Infection reports fetched periodically from the API
    val reports: Observable<List<CenReport>>

    // Store CEN from other device
    fun storeObservedCen(cen: Cen)

    // Send symptoms report
    fun sendReport(report: SymptomReport): Unit
}

class CoepiRepoImpl(
    private val cenRepo: CenRepo
) : CoEpiRepo {

    override val reports: Observable<List<CenReport>> = cenRepo.reports

    override fun sendReport(report: SymptomReport): Unit =
        cenRepo.sendReport(report)

    override fun storeObservedCen(cen: Cen) {
        cenRepo.storeCen(cen)
    }
}
