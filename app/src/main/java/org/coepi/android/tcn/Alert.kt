package org.coepi.android.tcn

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime

@Parcelize
data class Alert(
    var id: String,
    var report: PublicReport,
    var contactTime: UnixTime
) : Parcelable, Comparable<Alert> {
    override fun compareTo(other: Alert): Int {
        return when {
            this.contactTime.toDate().before(other.contactTime.toDate()) -> {
                -1
            }
            this.contactTime.toDate().after(other.contactTime.toDate()) -> {
                1
            }
            else -> 0
        };
    }
}
