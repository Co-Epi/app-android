package org.coepi.android.cen

import io.reactivex.subjects.BehaviorSubject
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.repo.RealmProvider

class RealmCenReportDao(private val realmProvider: RealmProvider) : CenReportDao {
    private val realm get() = realmProvider.realm

    override val reports: BehaviorSubject<List<ReceivedCenReport>> = BehaviorSubject.create()

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

    override fun insert(report: CenReport) {
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmCenReport>(report.id)
            realmObj.report = report.report
            realmObj.timestamp = report.timestamp
        }
    }

    override fun delete(report: SymptomReport) {
        val results = realm.where<RealmCenReport>()
            .equalTo("id", report.id)
            .findAll()

        realm.executeTransaction {
            results.deleteAllFromRealm()
        }
    }
}
