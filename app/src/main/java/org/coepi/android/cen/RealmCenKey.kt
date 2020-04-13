package org.coepi.android.cen

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.coepi.android.domain.CoEpiDate
import org.coepi.android.domain.CoEpiDate.Companion
import org.coepi.android.domain.CoEpiDate.Companion.fromSeconds
import java.util.Date

open class RealmCenKey(
    @PrimaryKey var key: String = "",
    var timestamp: Long = 0 // Unix time, seconds
): RealmObject()

fun RealmCenKey.toCenKey() = CenKey(key, fromSeconds(timestamp))
