package org.coepi.android.tcn

import io.realm.Sort.DESCENDING
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.domain.UnixTime
import org.coepi.android.repo.RealmProvider

class RealmTcnKeyDao(private val realmProvider: RealmProvider) : TcnKeyDao {
    private val realm get() = realmProvider.realm

    override fun lastTcnKeys(limit: Int): List<TcnKey> =
        realm.where<RealmTcnKey>()
            .sort("timestamp", DESCENDING)
            .limit(limit.toLong())
            .findAll()
            .map { TcnKey(it.key, UnixTime.fromValue(it.timestamp)) }

    override fun insert(key: TcnKey) {
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmTcnKey>(key.key)
            realmObj.timestamp = key.timestamp.value
        }
    }
}
