package org.coepi.android.cen

import io.realm.RealmObject

open class RealmReceivedCen(
    var cen: String = "",
    var timestamp: Int = 0
): RealmObject()
