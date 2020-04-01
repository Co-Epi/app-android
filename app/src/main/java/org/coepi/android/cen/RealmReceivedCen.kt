package org.coepi.android.cen

import io.realm.RealmObject

open class RealmReceivedCen(
    var cen: ByteArray = ByteArray(0),
    var timestamp: Int = 0
): RealmObject()
