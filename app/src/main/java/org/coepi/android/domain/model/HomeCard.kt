package org.coepi.android.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.ui.home.HomeCardId

@Parcelize
data class HomeCard(
    val cardId: HomeCardId,
    val title: String,
    val message: String
) : Parcelable