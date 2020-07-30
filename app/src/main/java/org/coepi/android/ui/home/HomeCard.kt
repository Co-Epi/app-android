package org.coepi.android.ui.home

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeCard(
    val id: HomeCardId,
    val title: String,
    val message: String,
    val hasNotification: Boolean = false,
    val notificationText: String = ""
) : Parcelable
