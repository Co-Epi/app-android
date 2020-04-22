package org.coepi.android.cen

import io.realm.kotlin.createObject
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import org.coepi.android.domain.CoEpiDate
import org.coepi.android.domain.CoEpiDate.Companion.fromUnixTime
import org.coepi.android.extensions.hexToByteArray
import org.coepi.android.repo.RealmProvider

class RealmCenDao(private val realmProvider: RealmProvider) : CenDao {
    private val realm get() = realmProvider.realm

    override fun all(): List<ReceivedCen> =
        realm.where<RealmReceivedCen>()
            .findAll()
            .map { it.toReceivedCen() }

    override fun matchCENs(start: CoEpiDate, end: CoEpiDate, cens: Array<String>): List<ReceivedCen> =
        realm.where<RealmReceivedCen>()
            .greaterThanOrEqualTo("timestamp", start.unixTime)
            .and()
            .lessThanOrEqualTo("timestamp", end.unixTime)
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
            realmObj.timestamp = cen.date.unixTime
        }
        return true
    }

    private fun RealmReceivedCen.toReceivedCen() =
        ReceivedCen(Cen(cen.hexToByteArray()), fromUnixTime(timestamp))
}
