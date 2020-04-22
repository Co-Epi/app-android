package org.coepi.android.cen

import io.realm.kotlin.createObject
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import org.coepi.android.domain.UnixTime
import org.coepi.android.extensions.hexToByteArray
import org.coepi.android.repo.RealmProvider

class RealmCenDao(private val realmProvider: RealmProvider) : CenDao {
    private val realm get() = realmProvider.realm

    override fun all(): List<ReceivedCen> =
        realm.where<RealmReceivedCen>()
            .findAll()
            .map { it.toReceivedCen() }

    override fun matchCENs(start: UnixTime, end: UnixTime, cens: Array<String>): List<ReceivedCen> =
        realm.where<RealmReceivedCen>()
            .greaterThanOrEqualTo("timestamp", start.value)
            .and()
            .lessThanOrEqualTo("timestamp", end.value)
            .and()
            .oneOf("cen", cens)
            .findAll()
            .map { it.toReceivedCen() }

    override fun findCen(cen: Cen): ReceivedCen? =
        realm.where<RealmReceivedCen>()
            .equalTo("cen", cen.toHex())
            .findAll()
            .firstOrNull()
            ?.toReceivedCen()

    override fun insert(cen: ReceivedCen): Boolean {
        if (findCen(cen.cen) != null) {
            return false
        }
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmReceivedCen>(cen.cen.toHex()) // Create a new object
            realmObj.timestamp = cen.timestamp.value
        }
        return true
    }

    private fun RealmReceivedCen.toReceivedCen() =
        ReceivedCen(Cen(cen.hexToByteArray()), UnixTime.fromValue(timestamp))
}
