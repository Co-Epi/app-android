package org.coepi.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.coepi.android.api.JniAlert
import org.coepi.android.api.JniAlertsArrayResult
import org.coepi.android.api.JniOneAlertResult
import org.coepi.android.api.JniPublicReport
import org.coepi.android.api.JniVoidResult
import org.coepi.android.api.NativeApi
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class JNIInterfaceTests {

    @Test
    fun testBootstrap() {
        val n = NativeApi()
        val value = n.testBootstrapCore("foo/bar", "info", true)
        assertEquals(JniVoidResult(1, ""), value)
    }

    @Test
    fun testFetchAReport() {
        val n = NativeApi()
        val value = n.testReturnAnAlert()
        assertEquals(
            JniOneAlertResult(
                1, "", JniAlert(
                    "123", JniPublicReport(
                        reportTime = 234324,
                        earliestSymptomTime = 1590356601,
                        feverSeverity = 1,
                        coughSeverity = 3,
                        breathlessness = true,
                        muscleAches = true,
                        lossSmellOrTaste = false,
                        diarrhea = false,
                        runnyNose = true,
                        other = false,
                        noSymptoms = true
                    ), 1592567315
                )
            ),
            value
        )
    }

    @Test
    fun testFetchNewReports() {
        val n = NativeApi()
        val value = n.testReturnMultipleAlerts()
        assertEquals(
            JniAlertsArrayResult(
                1, "", arrayOf(
                    JniAlert(
                        "123", JniPublicReport(
                            reportTime = 131321,
                            earliestSymptomTime = 1590356601,
                            feverSeverity = 1,
                            coughSeverity = 3,
                            breathlessness = true,
                            muscleAches = true,
                            lossSmellOrTaste = false,
                            diarrhea = false,
                            runnyNose = true,
                            other = false,
                            noSymptoms = true
                        ), 1592567315
                    ),
                    JniAlert(
                        "343356", JniPublicReport(
                            reportTime = 32516899200,
                            earliestSymptomTime = 1590356601,
                            feverSeverity = 1,
                            coughSeverity = 3,
                            breathlessness = true,
                            muscleAches = true,
                            lossSmellOrTaste = false,
                            diarrhea = false,
                            runnyNose = true,
                            other = false,
                            noSymptoms = true
                        ), 1592567315
                    )
                )
            ),
            value
        )
    }
}
