package org.coepi.android.ui.formatters

import org.coepi.android.R.string.unit_length_format_feet
import org.coepi.android.R.string.unit_length_format_meters
import org.coepi.android.system.Resources
import org.coepi.android.ui.formatters.NumberFormatters.twoDecimals
import org.coepi.core.domain.model.Length
import org.coepi.core.domain.model.LengthtUnit.FEET
import org.coepi.core.domain.model.LengthtUnit.METERS

class LengthFormatter(
    private val resources: Resources
) {
    fun format(measure: Length): String =
        when (measure.unit) {
            METERS -> resources.getString(
                unit_length_format_meters,
                twoDecimals.format(measure.value)
            )
            FEET -> resources.getString(
                unit_length_format_feet,
                twoDecimals.format(measure.value)
            )
        }
}
