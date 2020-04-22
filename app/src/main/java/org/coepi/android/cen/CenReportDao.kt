package org.coepi.android.cen

import io.reactivex.Observable

interface CenReportDao {
    val reports: Observable<List<ReceivedCenReport>>

    fun insert(report: CenReport)
    fun delete(report: SymptomReport)
}
