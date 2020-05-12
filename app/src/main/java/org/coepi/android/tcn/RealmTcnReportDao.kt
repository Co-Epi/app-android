package org.coepi.android.tcn

import io.reactivex.subjects.BehaviorSubject
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.repo.RealmProvider
import org.coepi.android.system.log.log

class RealmTcnReportDao(private val realmProvider: RealmProvider) : TcnReportDao {
    private val realm get() = realmProvider.realm

    override val reports: BehaviorSubject<List<ReceivedTcnReport>> = BehaviorSubject.create()

    private val reportsResults: RealmResults<RealmTcnReport>

    init {
        reportsResults = realm.where<RealmTcnReport>()
            .equalTo("deleted", false)
            .findAllAsync()

        reportsResults
            .addChangeListener { results, _ ->
                reports.onNext(results.map {
                    it.toReceivedTcnReport()
                })
            }
    }

    override fun all(): List<ReceivedTcnReport> =
        realm.where<RealmTcnReport>()
            .findAll()
            .map { it.toReceivedTcnReport() }

    override fun insert(report: TcnReport): Boolean {
        if (findReportById(report.id) != null) return false
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmTcnReport>(report.id)
            realmObj.report = report.memoStr
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

    private fun findReportById(id: String): RealmTcnReport? {
        val results = realm.where<RealmTcnReport>()
            .equalTo("id", id)
            .findAll()

        if (results.size > 1) {
            // Searching by id which is primary key, so can't have multiple results.
            throw IllegalStateException("Multiple results for report id: $id")
        } else {
            return results.firstOrNull()
        }
    }

    private fun RealmTcnReport.toReceivedTcnReport(): ReceivedTcnReport = ReceivedTcnReport(
        TcnReport(id = id, memoStr = report, timestamp = timestamp ))
}
