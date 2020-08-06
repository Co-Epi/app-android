package org.coepi.android.system.rx

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.BehaviorSubject.createDefault
import org.coepi.android.system.Preferences
import org.coepi.android.system.PreferencesKey.FILTER_ALERTS_WITH_LONG_DURATION
import org.coepi.android.system.PreferencesKey.FILTER_ALERTS_WITH_SHORT_DISTANCE
import org.coepi.android.system.PreferencesKey.FILTER_ALERTS_WITH_SYMPTOMS

interface ObservablePreferences {
    val filterAlertsWithSymptoms: Observable<Boolean>
    val filterAlertsWithLongDuration: Observable<Boolean>
    val filterAlertsWithShortDistance: Observable<Boolean>

    fun setFilterAlertsWithSymptoms(value: Boolean)
    fun setFilterAlertsWithLongDuration(value: Boolean)
    fun setFilterAlertsWithShortDistance(value: Boolean)
}

class ObservablePreferencesImpl(
    private val keyValueStore: Preferences
): ObservablePreferences {
    override val filterAlertsWithSymptoms: BehaviorSubject<Boolean> =
        createDefault(keyValueStore.getBoolean(FILTER_ALERTS_WITH_SYMPTOMS))
    override val filterAlertsWithLongDuration: BehaviorSubject<Boolean> =
        createDefault(keyValueStore.getBoolean(FILTER_ALERTS_WITH_LONG_DURATION))
    override val filterAlertsWithShortDistance: BehaviorSubject<Boolean> =
        createDefault(keyValueStore.getBoolean(FILTER_ALERTS_WITH_SHORT_DISTANCE))

    override fun setFilterAlertsWithSymptoms(value: Boolean) {
        keyValueStore.putBoolean(FILTER_ALERTS_WITH_SYMPTOMS, value)
        filterAlertsWithSymptoms.onNext(value)
    }

    override fun setFilterAlertsWithLongDuration(value: Boolean) {
        keyValueStore.putBoolean(FILTER_ALERTS_WITH_LONG_DURATION, value)
        filterAlertsWithLongDuration.onNext(value)
    }

    override fun setFilterAlertsWithShortDistance(value: Boolean) {
        keyValueStore.putBoolean(FILTER_ALERTS_WITH_SHORT_DISTANCE, value)
        filterAlertsWithShortDistance.onNext(value)
    }
}
