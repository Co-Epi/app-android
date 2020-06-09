package org.coepi.android.ui.alerts

import org.coepi.android.tcn.Alert

data class AlertViewData(
    val exposureType: String,
    val contactTime: String,
    val contactTimeMonth: String,
    var showMonthHeader: Boolean = true,
    val report: Alert
)
