package org.coepi.android.domain.model

import org.coepi.android.domain.model.LengthMeasurementUnit.FEET
import org.coepi.android.domain.model.LengthMeasurementUnit.METERS

enum class LengthMeasurementUnit {
    METERS, FEET
}

data class LengthMeasurement(val value: Float, val unit: LengthMeasurementUnit) {

    fun to(unit: LengthMeasurementUnit): LengthMeasurement =
        when {
            this.unit == METERS && unit == METERS -> this
            this.unit == METERS && unit == FEET ->
                LengthMeasurement(value * 3.28084f, unit)
            this.unit == FEET && unit == METERS ->
                LengthMeasurement(value * 0.3048f, unit)
            this.unit == FEET && unit == FEET -> this
            else -> error("Not handled: $this -> $unit")
        }
}
