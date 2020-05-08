package org.coepi.android.tcn

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmTcnReport(
    @PrimaryKey var id: String = "",
    var report: String = "",
    var timestamp: Long = 0,
    var deleted: Boolean = false
) : RealmObject()
