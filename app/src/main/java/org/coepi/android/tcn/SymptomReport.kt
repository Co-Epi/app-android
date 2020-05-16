package org.coepi.android.tcn

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime

@Parcelize
data class SymptomReport(
    var id: String,
    var report: PublicReport,
    var timestamp: UnixTime // TODO ReceivedTcn timestamp
) : Parcelable
