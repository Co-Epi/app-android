package org.coepi.android.tcn

import io.reactivex.Observable
import org.coepi.core.domain.model.Alert

interface AlertsDao {
    val alerts: Observable<List<Alert>>

    fun all(): List<Alert>
    fun insert(alert: Alert): Boolean
    fun delete(alert: Alert)
}
