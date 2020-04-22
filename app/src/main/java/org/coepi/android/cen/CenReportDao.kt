package org.coepi.android.cen

import io.reactivex.Observable

interface CenReportDao {
    val reports: Observable<List<ReceivedCenReport>>

    fun all(): List<ReceivedCenReport>
    fun insert(report: CenReport): Boolean
    fun delete(report: SymptomReport)
}
