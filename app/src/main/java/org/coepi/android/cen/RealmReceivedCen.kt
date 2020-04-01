package org.coepi.android.cen

import io.realm.RealmObject

open class RealmReceivedCen(
    var cen: String = "", // Hex encoding
    var timestamp: Int = 0
): RealmObject()
