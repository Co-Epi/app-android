package org.coepi.android.ui.alertsdetails

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.core.domain.model.Alert

@Parcelize
data class AlertDetailsViewData(
    val title: String,
    val contactStart: String,
    val contactDuration: String,
    val avgDistance: String,
    val minDistance: String,
    val reportTime: String,
    val symptoms: String,
    val alert: Alert,
    val showOtherExposuresHeader: Boolean
) : Parcelable

enum class LinkedAlertViewDataConnectionImage {
    Top, Body, Bottom;

    companion object {
        fun from(alertIndex: Int, alertsCount: Int): LinkedAlertViewDataConnectionImage =
            when (alertIndex) {
                0 -> Top
                alertsCount - 1 -> Bottom
                else -> Body
            }
    }
}

data class LinkedAlertViewData(
    val date: String,
    val contactStart: String,
    val contactDuration: String,
    val symptoms: String,
    val alert: Alert,
    val image: LinkedAlertViewDataConnectionImage,
    val bottomLine: Boolean
)
