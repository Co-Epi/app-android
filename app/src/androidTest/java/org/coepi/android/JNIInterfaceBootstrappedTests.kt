package org.coepi.android

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.coepi.android.api.JniAlertsArrayResult
import org.coepi.android.api.JniLogCallback
import org.coepi.android.api.JniVoidResult
import org.coepi.android.api.NativeApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests JNI functions that require that bootstrap was called in Rust.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class JNIInterfaceBootstrappedTests {

    private lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = androidx.test.core.app.ApplicationProvider.getApplicationContext()

        val dbPath = instrumentationContext.getDatabasePath("remove")
            // we need to pass the db directory (without file name)
            .absolutePath.removeSuffix("/remove")

        val n = NativeApi()
        val result = n.bootstrapCore(dbPath, "debug", true, JniLogCallback())
        // Double check
        assertEquals(JniVoidResult(1, ""), result)
    }

    // NOTE: Temporary test. We shouldn't test api calls.
    // TODO remove/comment when integration more stable
    // Alternatively we could add support for a flag in bootstrap to use testing mode (use mocks)
    @Test
    fun fetchNewReports() {
        val value = NativeApi().fetchNewReports()
        assertEquals(JniAlertsArrayResult(1, "", emptyArray()), value)
    }

    @Test
    fun recordTcn() {
        val value = NativeApi().recordTcn("2485a64b57addcaea3ed1b538d07dbce")
        assertEquals(JniVoidResult(1, ""), value)
    }

    @Test
    fun generateTcn() {
        val value = NativeApi().generateTcn()
        assertEquals(value.length, 32)
    }

    @Test
    fun setSymptomIds() {
        // NOTE: JSON format
        val value =
            NativeApi().setSymptomIds("""["breathlessness", "muscle_aches", "runny_nose"]""")
        assertEquals(JniVoidResult(1, ""), value)
    }

    @Test
    fun setInvalidSymptomIdReturnsError() {
        // NOTE: JSON format
        val value = NativeApi().setSymptomIds("""["not_supported", "muscle_aches", "runny_nose"]""")
        // TODO https://github.com/Co-Epi/app-backend-rust/issues/79 shouldn't return 1
        assertEquals(JniVoidResult(1, ""), value)
    }

    @Test
    fun setInvalidSymptomIdsJsonReturnsError() {
        val value = NativeApi().setSymptomIds("sdjfhskdf")
        // TODO https://github.com/Co-Epi/app-backend-rust/issues/79 shouldn't return 1
        assertEquals(JniVoidResult(1, ""), value)
    }

    @Test
    fun setCoughTypeNone() {
        val result = NativeApi().setCoughType("none")
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setCoughTypeWet() {
        val result = NativeApi().setCoughType("wet")
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setCoughTypeDry() {
        val result = NativeApi().setCoughType("dry")
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setInvalidCoughTypeReturnsError() {
        val result = NativeApi().setCoughType("invalid")
        // TODO https://github.com/Co-Epi/app-backend-rust/issues/79 shouldn't return 1
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setCoughDaysIsSet() {
        val result = NativeApi().setCoughDays(1, 3)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setCoughDaysIsNotSet() {
        // Note: days is ignored
        val result = NativeApi().setCoughDays(0, 123)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setCoughStatus() {
        val result = NativeApi().setCoughStatus("better_and_worse")
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setInvalidCoughStatusReturnsError() {
        val result = NativeApi().setCoughStatus("invalid")
        // TODO https://github.com/Co-Epi/app-backend-rust/issues/79 shouldn't return 1
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setBreathlessnessCause() {
        val result = NativeApi().setCoughStatus("leaving_house_or_dressing")
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setInvalidBreathlessnessCauseReturnsError() {
        val result = NativeApi().setCoughStatus("invalid")
        // TODO https://github.com/Co-Epi/app-backend-rust/issues/79 shouldn't return 1
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setFeverDaysIsSet() {
        val result = NativeApi().setFeverDays(1, 3)
        // TODO https://github.com/Co-Epi/app-backend-rust/issues/79 shouldn't return 1
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setFeverDaysNone() {
        // Note: days is ignored
        val result = NativeApi().setFeverDays(0, 3)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setFeverTakenTemperatureToday() {
        val result = NativeApi().setFeverTakenTemperatureToday(1, 3)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setFeverTakenTemperatureTodayNone() {
        // Note: days is ignored
        val result = NativeApi().setFeverTakenTemperatureToday(0, 3)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setFeverTakenTemperatureSpot() {
        val result = NativeApi().setFeverTakenTemperatureSpot("armpit")
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setInvalidFeverTakenTemperatureSpot() {
        val result = NativeApi().setFeverTakenTemperatureSpot("invalid")
        // TODO https://github.com/Co-Epi/app-backend-rust/issues/79 shouldn't return 1
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setHigherFeverTemperatureTaken() {
        val result = NativeApi().setFeverHighestTemperatureTaken(1, 100f)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setHigherFeverTemperatureTakenNone() {
        // Note: temp is ignored
        val result = NativeApi().setFeverHighestTemperatureTaken(0, 100f)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setEarliestSymptomStartedDaysAgo() {
        val result = NativeApi().setEarliestSymptomStartedDaysAgo(1, 10)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun setEarliestSymptomStartedDaysAgoNone() {
        // Note: days is ignored
        val result = NativeApi().setEarliestSymptomStartedDaysAgo(0, 10)
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun clearSymptoms() {
        val result = NativeApi().clearSymptoms()
        assertEquals(JniVoidResult(1, ""), result)
    }

    @Test
    fun submitSymptoms() {
        val result = NativeApi().submitSymptoms()
        assertEquals(JniVoidResult(1, ""), result)
    }

    // TODO more detailed tests, e.g. for each supported enum string (probably it makes sense to add
    // TODO constants in the app)
}
