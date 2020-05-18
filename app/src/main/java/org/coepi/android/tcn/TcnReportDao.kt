package org.coepi.android.tcn

import io.reactivex.Observable

interface TcnReportDao {
    val rawAlerts: Observable<List<RawAlert>>

    fun all(): List<RawAlert>
    fun insert(alert: RawAlert): Boolean
    fun delete(alert: Alert)
}
