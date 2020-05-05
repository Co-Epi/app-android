package org.coepi.android.tcn

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TcnReport(
    val id: String,
    val report: String, // Base64
    val timestamp: Long
) : Parcelable
