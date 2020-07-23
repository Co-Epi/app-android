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
    val alert: Alert
) : Parcelable
