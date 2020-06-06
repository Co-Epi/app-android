package org.coepi.android.tcn

import io.realm.kotlin.createObject
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import org.coepi.android.domain.UnixTime
import org.coepi.android.extensions.hexToByteArray
import org.coepi.android.repo.RealmProvider
import org.coepi.android.system.log.log

class RealmTcnDao(private val realmProvider: RealmProvider) : TcnDao {
    private val realm get() = realmProvider.realm

    override fun all(): List<ReceivedTcn> {
        // Note: Not ideal. We'll most likely drop Realm, so ok for now.
        realm.refresh()

        return realm.where<RealmReceivedTcn>()
            .findAll()
            .map { it.toReceivedTcn() }
    }

    override fun matchTcns(start: UnixTime, end: UnixTime, tcns: Array<String>): List<ReceivedTcn> =
        realm.where<RealmReceivedTcn>()
            .greaterThanOrEqualTo("timestamp", start.value)
            .and()
            .lessThanOrEqualTo("timestamp", end.value)
            .and()
            .oneOf("tcn", tcns)
            .findAll()
            .map { it.toReceivedTcn() }

    override fun findTcn(tcn: Tcn): ReceivedTcn? =
        realm.where<RealmReceivedTcn>()
            .equalTo("tcn", tcn.toHex())
            .findAll()
            .firstOrNull()
            ?.toReceivedTcn()

    override fun insert(tcn: ReceivedTcn): Boolean {
        if (findTcn(tcn.tcn) != null) {
            log.v("TCN was already in DB: $tcn")
            return false
        }
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmReceivedTcn>(tcn.tcn.toHex()) // Create a new object
            realmObj.timestamp = tcn.timestamp.value
        }
        return true
    }

    private fun RealmReceivedTcn.toReceivedTcn() =
        ReceivedTcn(Tcn(tcn.hexToByteArray()), UnixTime.fromValue(timestamp))
}
