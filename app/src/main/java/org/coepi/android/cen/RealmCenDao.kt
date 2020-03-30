package org.coepi.android.cen

import io.realm.kotlin.createObject
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import org.coepi.android.repo.RealmProvider

class RealmCenDao(private val realmProvider: RealmProvider) {
    private val realm get() = realmProvider.realm

    fun all(): List<RealmCen> =
        realm.where<RealmCen>().findAll()

    fun matchCENs(start: Int, end: Int, cens: Array<String>): List<RealmCen> =
        realm.where<RealmCen>()
            .greaterThanOrEqualTo("timestamp", start)
            .and()
            .lessThanOrEqualTo("timestamp", end)
            .and()
            .oneOf("cen", cens)
            .findAll()

    fun insert(cen: Cen) {
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmCen>() // Create a new object
            realmObj.cen = cen.cen
            realmObj.timestamp = cen.timestamp
        }
    }

//    @Delete("DELETE FROM cen where :timeStamp > timeStamp")
//    fun cleanCENs(timeStamp : Int)
}
