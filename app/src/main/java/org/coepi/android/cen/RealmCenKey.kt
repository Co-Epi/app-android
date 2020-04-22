package org.coepi.android.cen

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.coepi.android.domain.UnixTime

open class RealmCenKey(
    @PrimaryKey var key: String = "",
    var timestamp: Long = 0 // Unix time
): RealmObject()

fun RealmCenKey.toCenKey() = CenKey(key, UnixTime.fromValue(timestamp))
