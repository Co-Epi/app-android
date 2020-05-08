package org.coepi.android.tcn

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.coepi.android.domain.UnixTime

open class RealmTcnKey(
    @PrimaryKey var key: String = "",
    var timestamp: Long = 0 // Unix time
): RealmObject()
