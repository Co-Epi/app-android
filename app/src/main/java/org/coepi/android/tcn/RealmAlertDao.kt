package org.coepi.android.tcn

import io.reactivex.subjects.BehaviorSubject
import io.realm.RealmResults
import io.realm.kotlin.where
import org.coepi.android.repo.RealmProvider
import org.coepi.android.system.log.log
import org.coepi.core.domain.model.Alert
import org.coepi.core.domain.model.toInt
import io.realm.kotlin.createObject
import org.coepi.core.domain.model.UnixTime
import org.coepi.core.domain.model.UserInput.None
import org.coepi.core.domain.model.UserInput.Some
import org.coepi.core.domain.model.toCoughSeverity
import org.coepi.core.domain.model.toFeverSeverity

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
            realmObj.earliestSymptomTime = when (val earliestSymptomTime = alert.earliestSymptomTime) {
                is Some -> earliestSymptomTime.value.value
                is None -> null
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
            earliestSymptomTime = earliestSymptomTime?.let { Some(UnixTime.fromValue(it)) }
                ?: None,
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
