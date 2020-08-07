package org.coepi.android.system

import io.reactivex.Observable
import org.coepi.core.domain.model.LengthtUnit
import org.coepi.core.domain.model.LengthtUnit.FEET
import org.coepi.core.domain.model.LengthtUnit.METERS
import java.util.Locale

// If we wanted this to be more generic, we'd have a component
// for the unit system (metric/imperial) and another for the units.
// For the time being we'll use only meter/feet so simplifying.
interface UnitsProvider {
    val lengthUnit: Observable<LengthtUnit>
}

class UnitsProviderImpl(
    localeProvider: LocaleProvider
): UnitsProvider {
    override val lengthUnit: Observable<LengthtUnit> = localeProvider.locale.map {
        deriveLengthUnit(it)
    }

    private fun deriveLengthUnit(locale: Locale): LengthtUnit =
        when (locale.country.toUpperCase(locale)) {
            "US", "LR", "MM" -> FEET
            else -> METERS
        }
}
