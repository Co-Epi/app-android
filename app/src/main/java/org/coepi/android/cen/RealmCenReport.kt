package org.coepi.android.cen

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date

open class RealmCenReport(
    @PrimaryKey var id: String = "",
    var report: String = "",
    var timestamp: Long = 0,
    var deleted: Boolean = false
): RealmObject()
