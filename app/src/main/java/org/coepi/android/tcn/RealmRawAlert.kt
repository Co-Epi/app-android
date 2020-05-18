package org.coepi.android.tcn

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmRawAlert(
    @PrimaryKey var id: String = "",
    var report: String = "",
    var timestamp: Long = 0, // Unix time
    var deleted: Boolean = false
) : RealmObject()
