package org.coepi.android.cen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.model.Symptom

@Parcelize
data class CenReport(
    val id: String,
    val report: String,
    val timestamp: Long
) : Parcelable {

    fun toSymptomsList() = listOf(
        Symptom("1", "TODO extract symptoms from report"),
        Symptom("2", "TODO extract symptoms from report"),
        Symptom("3", "TODO extract symptoms from report")
    )
}
