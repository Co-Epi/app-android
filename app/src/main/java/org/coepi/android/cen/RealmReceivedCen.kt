package org.coepi.android.cen

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmReceivedCen(
    @PrimaryKey var cen: String = "", // Hex encoding
    var timestamp: Long = 0
): RealmObject()
