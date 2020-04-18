package org.coepi.android.cen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CenReport(
    val id: String,
    val report: String,
    val timestamp: Long
) : Parcelable
