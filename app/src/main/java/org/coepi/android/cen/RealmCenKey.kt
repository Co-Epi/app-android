package org.coepi.android.cen

import io.realm.RealmObject
import java.util.Date

open class RealmCenKey(
    var key: String = "",
    var timestamp: Long = 0
): RealmObject()

fun RealmCenKey.toCenKey() = CenKey(key, timestamp)
