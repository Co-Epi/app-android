package org.coepi.android.components.noop

import io.reactivex.Observable
import org.coepi.android.tcn.Alert
import org.coepi.android.tcn.RawAlert
import org.coepi.android.tcn.TcnReportDao

class NoOpTcnReportsDao: TcnReportDao {
    override val alerts: Observable<List<RawAlert>> =
        Observable.just(emptyList())
    override fun all(): List<RawAlert> = emptyList()
    override fun insert(alert: RawAlert): Boolean = false
    override fun delete(alert: Alert) {}
}
