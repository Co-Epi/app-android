package org.coepi.android.cen

import io.reactivex.subjects.BehaviorSubject
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.oneOf
import io.realm.kotlin.where
import org.coepi.android.repo.RealmProvider

class RealmCenReportDao(private val realmProvider: RealmProvider) {
    private val realm get() = realmProvider.realm

    val reports: BehaviorSubject<List<ReceivedCenReport>> = BehaviorSubject.create()

    private val reportsResults: RealmResults<RealmCenReport>

    init {
        reportsResults = realm.where<RealmCenReport>().findAllAsync()
        reportsResults.addChangeListener { results, _ ->
            reports.onNext(results.map {
                ReceivedCenReport(
                    CenReport(id = it.id, report = it.report, timestamp = it.timestamp ))
            })
        }
    }

    fun all(limit : Int): List<RealmCenReport> =
        realm.where<RealmCenReport>()
            .findAll()

    fun loadAllById(id: Array<String>): List<RealmCenReport> =
        realm.where<RealmCenReport>()
            .oneOf("id", id)
            .findAll()

    fun findByRange(start: Long, end: Long): List<RealmCenReport> =
        realm.where<RealmCenReport>()
            .greaterThanOrEqualTo("timestamp", start)
            .and()
            .lessThanOrEqualTo("timestamp", end)
            .limit(1) // TODO why limit 1?
            .findAll()

    fun insert(report: CenReport) {
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmCenReport>(report.id)
            realmObj.report = report.report
        }
    }

    fun delete(report: ReceivedCenReport) {
        val results = realm.where<RealmCenReport>()
            .equalTo("id", report.report.id)
            .findAll()

        realm.executeTransaction {
            results.deleteAllFromRealm()
        }
    }
}
