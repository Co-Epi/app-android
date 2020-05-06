package org.coepi.android.components.noop

import io.reactivex.Observable
import org.coepi.android.tcn.ReceivedTcnReport
import org.coepi.android.tcn.SymptomReport
import org.coepi.android.tcn.TcnReport
import org.coepi.android.tcn.TcnReportDao

class NoOpTcnReportsDao: TcnReportDao {
    override val reports: Observable<List<ReceivedTcnReport>> =
        Observable.just(emptyList())
    override fun all(): List<ReceivedTcnReport> = emptyList()
    override fun insert(report: TcnReport): Boolean = false
    override fun delete(report: SymptomReport) {}
}
