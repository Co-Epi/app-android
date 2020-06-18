package org.coepi.android.api.publicreport

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.UserInput

@Parcelize
data class PublicReport(
    val reportTime: UnixTime,
    val earliestSymptomTime: UserInput<UnixTime>,
    val feverSeverity: FeverSeverity,
    val coughSeverity: CoughSeverity,
    val breathlessness: Boolean,
    val muscleAches: Boolean,
    val lossSmellOrTaste: Boolean,
    val diarrhea: Boolean,
    val runnyNose: Boolean,
    val other: Boolean,
    val noSymptoms: Boolean // https://github.com/Co-Epi/app-ios/issues/268#issuecomment-645583717
): Parcelable

enum class FeverSeverity {
    NONE, MILD, SERIOUS
}

enum class CoughSeverity {
    NONE, EXISTING, WET, DRY
}
