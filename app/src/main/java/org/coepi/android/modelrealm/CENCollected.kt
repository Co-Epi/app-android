package org.coepi.android.modelrealm

import io.realm.RealmObject
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.util.Date

//for realm see:  https://realm.io/docs/kotlin/latest/
/**
 * As specified here
 * the mobile will collect CEN (Contact Event Number) ober BLE broadcasted by others
 * k_contact is generated every week?
 * CEN = E ( k_contact, common64 << 64 | random 64 )
 * these will be used contacting the backend to obtain the key_infected
 * if ( D( k_infected, CEN_i ) >> 64  == common64 ) { /*store infection */ }
 */
open class CENCollected (
    // You can put properties in the constructor as long as
    // all of them are initialized with default values. This
    // ensures that an empty constructor is generated.
    // All properties are by default persisted.
    // Non-nullable properties must be initialized
    // with non-null values.
    var CEN: String = "",
    var date: Date = Date()
    ): RealmObject()
