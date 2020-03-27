package org.coepi.android.modelrealm

import io.realm.RealmObject
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.Date

//for realm see:  https://realm.io/docs/kotlin/latest/

/**
 * symptoms that have been diagnosticec on the owner of the phone
 */
open class SymptomsOwner (

    var key: String = "",
    var symptom: String = "Sympton",
    var sent: Boolean = false
    ): RealmObject()