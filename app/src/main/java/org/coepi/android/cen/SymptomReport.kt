package org.coepi.android.cen

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.model.Symptom

@Parcelize
data class SymptomReport(
    var id: String,
    var symptoms: List<Symptom>,
    var timestamp: UnixTime
) : Parcelable
