package org.coepi.android.cen

import io.realm.Sort.DESCENDING
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.domain.CoEpiDate.Companion.fromUnixTime
import org.coepi.android.repo.RealmProvider

class RealmCenKeyDao(private val realmProvider: RealmProvider) : CenKeyDao {
    private val realm get() = realmProvider.realm

    override fun lastCENKeys(limit : Int): List<CenKey> =
        realm.where<RealmCenKey>()
            .sort("timestamp", DESCENDING)
            .limit(limit.toLong())
            .findAll()
            .map { CenKey(it.key, fromUnixTime(it.timestamp)) }

    override fun insert(key: CenKey) {
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmCenKey>(key.key)
            realmObj.timestamp = key.date.unixTime
        }
    }
}
