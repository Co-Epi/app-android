package org.coepi.android.api.publicreport

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.UserInput

@Parcelize
data class PublicReport(
    val earliestSymptomTime: UserInput<UnixTime>,
    val feverSeverity: FeverSeverity,
    val coughSeverity: CoughSeverity,
    val breathlessness: Boolean
): Parcelable

enum class FeverSeverity {
    NONE, MILD, SERIOUS
}

enum class CoughSeverity {
    NONE, EXISTING, WET, DRY
}

fun PublicReport.shouldBeSent() =
    feverSeverity != FeverSeverity.NONE || coughSeverity != CoughSeverity.NONE || breathlessness
