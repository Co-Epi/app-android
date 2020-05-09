package org.coepi.android.tcn

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TcnReport(
    val id: String,
    val memoStr: String, // Base64
    val timestamp: Long
) : Parcelable
