package org.coepi.android.tcn

import io.reactivex.subjects.BehaviorSubject
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.domain.UnixTime
import org.coepi.android.repo.RealmProvider
import org.coepi.android.system.log.log

class RealmRawAlertDao(private val realmProvider: RealmProvider) : TcnReportDao {
    private val realm get() = realmProvider.realm

    override val rawAlerts: BehaviorSubject<List<RawAlert>> = BehaviorSubject.create()

    private val reportsResults: RealmResults<RealmRawAlert>

    init {
        reportsResults = realm.where<RealmRawAlert>()
            .equalTo("deleted", false)
            .findAllAsync()

        reportsResults
            .addChangeListener { results, _ ->
                rawAlerts.onNext(results.map {
                    it.toRawAlert()
                })
            }
    }

    override fun all(): List<RawAlert> =
        realm.where<RealmRawAlert>()
            .findAll()
            .map { it.toRawAlert() }

    override fun insert(alert: RawAlert): Boolean {
        if (findAlertById(alert.id) != null) return false
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmRawAlert>(alert.id)
            realmObj.report = alert.memoStr
            realmObj.timestamp = alert.contactTime.value
        }
        return true
    }

    override fun delete(alert: Alert) {
        log.d("ACKing report: $alert")

        val realmRawAlert = findAlertById(alert.id) ?:
            log.w("Couldn't find alert to delete: $alert.").run { return }

        realm.executeTransaction {
            realmRawAlert.deleted = true
        }
    }

    private fun findAlertById(id: String): RealmRawAlert? {
        val results = realm.where<RealmRawAlert>()
            .equalTo("id", id)
            .findAll()

        if (results.size > 1) {
            // Searching by id which is primary key, so can't have multiple results.
            throw IllegalStateException("Multiple results for report id: $id")
        } else {
            return results.firstOrNull()
        }
    }

    private fun RealmRawAlert.toRawAlert(): RawAlert =
        RawAlert(id = id, memoStr = report, contactTime = UnixTime.fromValue(timestamp))
}
