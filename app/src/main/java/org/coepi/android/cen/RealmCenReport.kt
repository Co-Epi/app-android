package org.coepi.android.cen

import io.realm.RealmObject
import java.util.Date

open class RealmCenReport(
    var id: String="",
    var report: String = "",
    var timestamp: Long = 0
): RealmObject()
