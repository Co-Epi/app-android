package org.coepi.android.cen

import io.reactivex.subjects.BehaviorSubject
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.repo.RealmProvider
import org.coepi.android.system.log.log

class RealmCenReportDao(private val realmProvider: RealmProvider) : CenReportDao {
    private val realm get() = realmProvider.realm

    override val reports: BehaviorSubject<List<ReceivedCenReport>> = BehaviorSubject.create()

    private val reportsResults: RealmResults<RealmCenReport>

    init {
        reportsResults = realm.where<RealmCenReport>()
            .equalTo("deleted", false)
            .findAllAsync()

        reportsResults
            .addChangeListener { results, _ ->
                reports.onNext(results.map {
                    it.toReceivedCenReport()
                })
            }
    }

    override fun all(): List<ReceivedCenReport> =
        realm.where<RealmCenReport>()
            .findAll()
            .map { it.toReceivedCenReport() }

    override fun insert(report: CenReport): Boolean {
        if (findReportById(report.id) != null) return false
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmCenReport>(report.id)
            realmObj.report = report.report
            realmObj.timestamp = report.timestamp
        }
        return true
    }

    override fun delete(report: SymptomReport) {
        log.d("ACKing report: $report")

        val realmReport = findReportById(report.id) ?:
            log.w("Couldn't find report to delete: $report.").run { return }

        realm.executeTransaction {
            realmReport.deleted = true
        }
    }

    private fun findReportById(id: String): RealmCenReport? {
        val results = realm.where<RealmCenReport>()
            .equalTo("id", id)
            .findAll()

        if (results.size > 1) {
            // Searching by id which is primary key, so can't have multiple results.
            throw IllegalStateException("Multiple results for report id: $id")
        } else {
            return results.firstOrNull()
        }
    }

    private fun RealmCenReport.toReceivedCenReport(): ReceivedCenReport = ReceivedCenReport(
        CenReport(id = id, report = report, timestamp = timestamp ))
}
