package org.coepi.android.ui.formatters

import org.coepi.android.R.string.unit_length_format_feet
import org.coepi.android.R.string.unit_length_format_meters
import org.coepi.android.domain.model.LengthMeasurement
import org.coepi.android.domain.model.LengthMeasurementUnit.FEET
import org.coepi.android.domain.model.LengthMeasurementUnit.METERS
import org.coepi.android.system.Resources
import org.coepi.android.ui.formatters.NumberFormatters.oneDecimal

class MeasurementFormatter(
    private val resources: Resources
) {
    fun format(measure: LengthMeasurement): String =
        when (measure.unit) {
            METERS -> resources.getString(
                unit_length_format_meters,
                oneDecimal.format(measure.value)
            )
            FEET -> resources.getString(
                unit_length_format_feet,
                oneDecimal.format(measure.value)
            )
        }
}
