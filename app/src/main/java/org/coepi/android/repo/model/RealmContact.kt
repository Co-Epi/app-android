package org.coepi.android.repo.model

import io.realm.RealmObject
import java.util.Date

open class RealmContact(
    var tcn: String = "",
    var date: Date = Date()
): RealmObject()
