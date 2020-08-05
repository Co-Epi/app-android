package org.coepi.android.system

import io.reactivex.Observable
import org.coepi.android.domain.model.LengthMeasurementUnit
import org.coepi.android.domain.model.LengthMeasurementUnit.FEET
import org.coepi.android.domain.model.LengthMeasurementUnit.METERS
import java.util.Locale

// If we wanted this to be more generic, we'd have a component
// for the unit system (metric/imperial) and another for the units.
// For the time being we'll use only meter/feet so simplifying.
interface UnitSystemProvider {
    val measureUnit: Observable<LengthMeasurementUnit>
}

class UnitSystemProviderImpl(
    localeProvider: LocaleProvider
): UnitSystemProvider {
    override val measureUnit: Observable<LengthMeasurementUnit> = localeProvider.locale.map {
        deriveMeasureUnit(it)
    }

    private fun deriveMeasureUnit(locale: Locale): LengthMeasurementUnit =
        when (locale.country.toUpperCase(locale)) {
            "US", "LR", "MM" -> FEET
            else -> METERS
        }
}
