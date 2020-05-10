package org.coepi.android.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.symptomflow.SymptomInputs.Breathlessness.Cause

@Parcelize
data class Breathless(
    val id: Cause,
    val name: String,
    val icon: String
) : Parcelable
