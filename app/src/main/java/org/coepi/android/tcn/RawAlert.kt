package org.coepi.android.tcn

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.UnixTime

@Parcelize
data class RawAlert(
    val id: String,
    val memoStr: String, // Base64
    val contactTime: UnixTime
) : Parcelable
