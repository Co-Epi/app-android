package org.coepi.android.tcn

import io.reactivex.subjects.BehaviorSubject
import io.realm.RealmResults
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.UserInput
import org.coepi.android.repo.RealmProvider
import org.coepi.android.system.log.log

class RealmAlertDao(private val realmProvider: RealmProvider) : AlertsDao {
    private val realm get() = realmProvider.realm

    override val alerts: BehaviorSubject<List<Alert>> = BehaviorSubject.create()

    private val reportsResults: RealmResults<RealmAlert>

    init {
        reportsResults = realm.where<RealmAlert>()
            .equalTo("deleted", false)
            .findAllAsync()

        reportsResults
            .addChangeListener { results, _ ->
                alerts.onNext(results.map { it.toAlert() })
            }
    }

    override fun all(): List<Alert> =
        realm.where<RealmAlert>()
            .findAll()
            .map { it.toAlert() }

    override fun insert(alert: Alert): Boolean {
        if (findAlertById(alert.id) != null) return false
        realm.executeTransaction {
            val realmObj = realm.createObject<RealmAlert>(alert.id)

            realmObj.contactTime = alert.contactTime.value
            realmObj.reportTime = alert.reportTime.value
            realmObj.earliestSymptomTime = when (alert.earliestSymptomTime) {
                is UserInput.Some -> alert.earliestSymptomTime.value.value
                is UserInput.None -> null
            }
            realmObj.feverSeverity = alert.feverSeverity.toInt()
            realmObj.coughSeverity = alert.coughSeverity.toInt()
            realmObj.breathlessness = alert.breathlessness
            realmObj.muscleAches = alert.muscleAches
            realmObj.lossSmellOrTaste = alert.lossSmellOrTaste
            realmObj.diarrhea = alert.diarrhea
            realmObj.runnyNose = alert.runnyNose
            realmObj.other = alert.other
            realmObj.noSymptoms = alert.noSymptoms

            realmObj.deleted = false
        }
        return true
    }

    override fun delete(alert: Alert) {
        log.d("ACKing report: $alert")

        val realmRawAlert =
            findAlertById(alert.id) ?: log.w("Couldn't find alert to delete: $alert.")
                .run { return }

        realm.executeTransaction {
            realmRawAlert.deleted = true
        }
    }

    private fun findAlertById(id: String): RealmAlert? {
        val results = realm.where<RealmAlert>()
            .equalTo("id", id)
            .findAll()

        if (results.size > 1) {
            // Searching by id which is primary key, so can't have multiple results.
            throw IllegalStateException("Multiple results for report id: $id")
        } else {
            return results.firstOrNull()
        }
    }

    private fun RealmAlert.toAlert(): Alert =
        Alert(
            id = id,
            reportTime = UnixTime.fromValue(reportTime),
            earliestSymptomTime = earliestSymptomTime?.let { UserInput.Some(UnixTime.fromValue(it)) }
                ?: UserInput.None,
            feverSeverity = toFeverSeverity(feverSeverity),
            coughSeverity = toCoughSeverity(coughSeverity),
            breathlessness = breathlessness,
            muscleAches = muscleAches,
            lossSmellOrTaste = lossSmellOrTaste,
            diarrhea = diarrhea,
            runnyNose = runnyNose,
            other = other,
            noSymptoms = noSymptoms,
            contactTime = UnixTime.fromValue(contactTime)

        )
}
