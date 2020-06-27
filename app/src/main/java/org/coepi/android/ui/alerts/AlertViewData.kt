package org.coepi.android.ui.alerts

import org.coepi.core.domain.model.Alert

sealed class AlertCellViewData {
    data class Header(val text: String) : AlertCellViewData()
    data class Item(val viewData: AlertViewData) : AlertCellViewData()
}

data class AlertViewData(
    val exposureType: String,
    val contactTime: String,
    val contactTimeMonth: String,
    val alert: Alert
)
