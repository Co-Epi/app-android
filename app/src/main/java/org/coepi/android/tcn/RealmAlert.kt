package org.coepi.android.tcn

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmAlert(
    @PrimaryKey var id: String = "",

    var contactTime: Long = -1,
    var reportTime: Long = -1,
    var earliestSymptomTime: Long? = null,

    var feverSeverity: Int = -1,
    var coughSeverity: Int = -1,
    var breathlessness: Boolean = false,
    var muscleAches: Boolean = false,
    var lossSmellOrTaste: Boolean = false,
    var diarrhea: Boolean = false,
    var runnyNose: Boolean = false,
    var other: Boolean = false,
    var noSymptoms: Boolean = false, // https://github.com/Co-Epi/app-ios/issues/268#issuecomment-645583717

    var deleted: Boolean = false
) : RealmObject()
