package org.coepi.android.cen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReceivedCenReport(
    val report: CenReport
) : Parcelable
