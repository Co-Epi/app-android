package org.coepi.android.tcn

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReceivedTcnReport(
    val report: TcnReport
) : Parcelable
