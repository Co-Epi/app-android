package org.coepi.android

import org.junit.Test
import org.junit.Assert.*
import org.coepi.android.domain.model.Temperature.*

class TemperatureUnitTest {
    @Test
    fun celsiusToFahrenheit_isCorrect() {
        assertEquals(
            Fahrenheit(32.0.toFloat()),
            Celsius(0.0.toFloat()).toFarenheit()
        )
    }

    @Test
    fun fahrenheitToCelsius_isCorrect() {
        assertEquals(
            Celsius(0.0.toFloat()),
            Fahrenheit(32.0.toFloat()).toCelsius()
        )
    }
}