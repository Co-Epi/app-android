package org.coepi.android.repo

import android.content.Context
import io.realm.Realm
import io.realm.Realm.getDefaultInstance

class RealmProvider(private val applicationContext: Context) {
    init {
        Realm.init(applicationContext)
    }

    val realm: Realm get() = getDefaultInstance()
}
