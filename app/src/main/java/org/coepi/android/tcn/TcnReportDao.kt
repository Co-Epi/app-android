package org.coepi.android.tcn

import io.reactivex.Observable

interface TcnReportDao {
    val reports: Observable<List<ReceivedTcnReport>>

    fun all(): List<ReceivedTcnReport>
    fun insert(report: TcnReport): Boolean
    fun delete(report: SymptomReport)
}
