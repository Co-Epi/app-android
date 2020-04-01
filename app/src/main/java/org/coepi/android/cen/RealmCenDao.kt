package org.coepi.android.cen

import android.util.Base64
import io.realm.kotlin.createObject
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import org.coepi.android.repo.RealmProvider

class RealmCenDao(private val realmProvider: RealmProvider) {
    private val realm get() = realmProvider.realm

    fun all(): List<RealmReceivedCen> =
        realm.where<RealmReceivedCen>().findAll()

    fun matchCENs(start: Int, end: Int, cens: Array<String>): List<RealmReceivedCen> =
        realm.where<RealmReceivedCen>()
            .greaterThanOrEqualTo("timestamp", start)
            .and()
            .lessThanOrEqualTo("timestamp", end)
            .and()
            .oneOf("cen", cens)
            .findAll()

    fun insert(cen: ReceivedCen) {
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmReceivedCen>() // Create a new object
            realmObj.cen = Base64.encodeToString(cen.cen.bytes, Base64.NO_WRAP)
            realmObj.timestamp = cen.timestamp
        }
    }

//    @Delete("DELETE FROM cen where :timeStamp > timeStamp")
//    fun cleanCENs(timeStamp : Int)
}
