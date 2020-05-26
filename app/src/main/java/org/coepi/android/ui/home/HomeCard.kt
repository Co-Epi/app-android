package org.coepi.android.ui.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeCard(
    val cardId: HomeCardId,
    val title: String,
    val message: String,
    var newAlerts: Boolean = false,
    var newAlertsTitle: String = ""
) : Parcelable
