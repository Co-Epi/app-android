package org.coepi.android.tcn

import io.reactivex.Observable

interface AlertsDao {
    val alerts: Observable<List<Alert>>

    fun all(): List<Alert>
    fun insert(alert: Alert): Boolean
    fun delete(alert: Alert)
}
