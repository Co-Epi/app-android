package org.coepi.android.cen

import io.realm.RealmObject

open class RealmCen(
    var cen: String = "",
    var timestamp: Int = 0
): RealmObject()
