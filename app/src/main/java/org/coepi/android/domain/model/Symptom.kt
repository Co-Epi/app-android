package org.coepi.android.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.symptomflow.SymptomId

@Parcelize
data class Symptom(
    val id: SymptomId,
    val name: String
) : Parcelable
