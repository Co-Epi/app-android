package org.coepi.android.tcn

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomInputs

@Parcelize
data class SymptomReport(
    var id: String,
    var inputs: SymptomInputs,
    var timestamp: UnixTime
) : Parcelable
