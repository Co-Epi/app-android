package org.coepi.android.repo

import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.ObservablePreferences
import org.coepi.android.ui.extensions.durationSeconds
import org.coepi.core.domain.model.Alert
import org.coepi.core.domain.model.LengthMeasurement

interface ObservableAlertFilters {
    val filters: Observable<AlertFilters>
}

class ObservableAlertFiltersImpl(
    preferences: ObservablePreferences,
    filterSettings: AlertFilterSettings
) : ObservableAlertFilters {
    override val filters: Observable<AlertFilters>

    init {
        filters = Observables.combineLatest(
            preferences.filterAlertsWithSymptoms,
            preferences.filterAlertsWithLongDuration,
            preferences.filterAlertsWithShortDistance)
            .map { (withSymptoms, withLongDuration, withShortDistance) ->
                AlertFilters(
                    withSymptoms = withSymptoms,
                    withLongDuration = withLongDuration,
                    withShortDistance = withShortDistance,
                    settings = filterSettings
                )
            }
            .doOnNext { log.d("Alert filters updated: $it") }
    }
}

data class AlertFilterSettings(
    val durationSecondsLargerThan: Int,
    val distanceShorterThan: LengthMeasurement
)

data class AlertFilters(
    val withSymptoms: Boolean,
    val withLongDuration: Boolean,
    val withShortDistance: Boolean,
    val settings: AlertFilterSettings
)

fun AlertFilters.apply(alerts: List<Alert>): List<Alert> =
    alerts.filter { alert ->
        apply(
            filter = withSymptoms,
            meetsCondition = { !alert.noSymptoms }
        ) &&
        apply(
            filter = withLongDuration,
            meetsCondition = {
                alert.durationSeconds > settings.durationSecondsLargerThan
            }
        ) &&
        apply(
            filter = withShortDistance,
            meetsCondition = {
                alert.avgDistance.toFeet().value < settings.distanceShorterThan.toFeet().value
            }
        )
    }

private fun apply(filter: Boolean, meetsCondition: () -> Boolean): Boolean = if (filter) {
    meetsCondition()
} else {
    true
}
