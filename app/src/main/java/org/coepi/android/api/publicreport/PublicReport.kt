package org.coepi.android.api.publicreport

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomInputs.Diarrhea
import org.coepi.android.domain.symptomflow.SymptomInputs.LossSmellOrTaste
import org.coepi.android.domain.symptomflow.SymptomInputs.MuscleAches
import org.coepi.android.domain.symptomflow.SymptomInputs.Other
import org.coepi.android.domain.symptomflow.SymptomInputs.RunnyNose
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
    val other: Boolean
): Parcelable

enum class FeverSeverity {
    NONE, MILD, SERIOUS
}

enum class CoughSeverity {
    NONE, EXISTING, WET, DRY
}

fun PublicReport.shouldBeSent() =
    feverSeverity != FeverSeverity.NONE || coughSeverity != CoughSeverity.NONE || breathlessness
            || muscleAches || lossSmellOrTaste || diarrhea || runnyNose || other
