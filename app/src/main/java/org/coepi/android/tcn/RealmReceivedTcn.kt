package org.coepi.android.tcn

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmReceivedTcn(
    @PrimaryKey var tcn: String = "", // Hex encoding
    var timestamp: Long = 0 // Unix time
): RealmObject()
