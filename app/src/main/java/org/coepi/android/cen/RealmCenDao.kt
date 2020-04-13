package org.coepi.android.cen

import io.realm.kotlin.createObject
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import org.coepi.android.domain.CoEpiDate
import org.coepi.android.repo.RealmProvider

class RealmCenDao(private val realmProvider: RealmProvider) {
    private val realm get() = realmProvider.realm

    fun all(): List<RealmReceivedCen> =
        realm.where<RealmReceivedCen>().findAll()

    fun matchCENs(start: CoEpiDate, end: CoEpiDate, cens: Array<String>): List<RealmReceivedCen> =
        realm.where<RealmReceivedCen>()
            .greaterThanOrEqualTo("timestamp", start.unixTime)
            .and()
            .lessThanOrEqualTo("timestamp", end.unixTime)
            .and()
            .oneOf("cen", cens)
            .findAll()

    private fun findCen(cen: Cen): RealmReceivedCen? =
        realm.where<RealmReceivedCen>()
            .equalTo("cen", cen.toHex())
            .findAll()
            .firstOrNull()

    fun insert(cen: ReceivedCen): Boolean {
        if (findCen(cen.cen) != null) {
            return false
        }
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmReceivedCen>(cen.cen.toHex()) // Create a new object
            realmObj.timestamp = cen.date.unixTime
        }
        return true
    }

//    @Delete("DELETE FROM cen where :timeStamp > timeStamp")
//    fun cleanCENs(timeStamp : Int)
}
