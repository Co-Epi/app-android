package org.coepi.android.ui.alerts

import org.coepi.android.cen.ReceivedCenReport

data class AlertViewData(val exposureType: String, val time: String, val report: ReceivedCenReport)
