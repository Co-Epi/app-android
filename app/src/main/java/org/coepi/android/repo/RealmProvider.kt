package org.coepi.android.repo

import android.content.Context
import io.realm.Realm
import io.realm.Realm.getDefaultInstance
import io.realm.RealmConfiguration
import io.realm.RealmConfiguration.Builder

class RealmProvider(applicationContext: Context) {
    init {
        Realm.init(applicationContext)
        Realm.setDefaultConfiguration(defaultConfig())
    }

    val realm: Realm get() = getDefaultInstance()

    private fun defaultConfig(): RealmConfiguration = Builder()
        .deleteRealmIfMigrationNeeded() // Useful during early development: Reset data if the schema changes
        .build()
}
