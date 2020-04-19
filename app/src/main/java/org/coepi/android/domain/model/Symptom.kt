package org.coepi.android.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Symptom(
    val id: String,
    val name: String
) : Parcelable
