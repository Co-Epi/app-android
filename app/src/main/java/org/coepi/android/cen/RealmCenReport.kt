package org.coepi.android.cen

import io.realm.RealmObject
import java.util.Date

open class RealmCenReport(
    var id: String="",
    var report: String = "",
    var keys: String = "",
    var reportMimeType: String = "",
    var date: Date = Date(),
    var isUser: Boolean = false
): RealmObject()
