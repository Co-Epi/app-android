package org.coepi.android.cen

import io.realm.Sort.DESCENDING
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.repo.RealmProvider

class RealmCenLastKeysCheckDao(private val realmProvider: RealmProvider) {
    private val realm get() = realmProvider.realm

    fun lastCENKeysCheck(limit : Int): List<RealmCenLastKeysCheck> =
        realm.where<RealmCenLastKeysCheck>()
            .sort("timestamp", DESCENDING)
            .limit(limit.toLong())
            .findAll()

    fun insert(timestamp: Int) {
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmCenLastKeysCheck>()
            realmObj.timestamp = timestamp
        }
    }

//    @Query("SELECT * FROM cenkey WHERE :first <= timeStamp AND timeStamp <= :last LIMIT 1")
//    fun findByRange(first: Int?, last: Int?): List<CENKey>?

//    @Delete
//    fun deleteBefore(timestamp : Int)
}
