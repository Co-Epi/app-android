package org.coepi.android.modelrealm

import io.realm.RealmObject
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.Date

//for realm see:  https://realm.io/docs/kotlin/latest/
/**
 * the symptons and key polled every hour
 */
open class SymptomsReceived (
    // You can put properties in the constructor as long as
    // all of them are initialized with default values. This
    // ensures that an empty constructor is generated.
    // All properties are by default persisted.
    // Non-nullable properties must be initialized
    // with non-null values.
    var key: String = "",
    var symptom: String = "Sympton",
    var matchfound : Boolean = false/*,
    var cenmatch : CENCollected = null*/
    ): RealmObject()