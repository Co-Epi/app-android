package org.coepi.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.coepi.android.api.JniAlert
import org.coepi.android.api.JniAlertsArrayResult
import org.coepi.android.api.JniLogCallback
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
        val n = org.coepi.android.api.NativeApi()
        val value = n.bootstrapCore(
            "foo/bar", "info", true,
            org.coepi.android.api.JniLogCallback()
        )
        assertEquals(org.coepi.android.api.JniVoidResult(1, ""), value)
    }

    @Test
    fun testFetchAReport() {
        val n = org.coepi.android.api.NativeApi()
        val value = n.testReturnAnAlert()
        assertEquals(
            org.coepi.android.api.JniOneAlertResult(
                1, "", org.coepi.android.api.JniAlert(
                    "123", org.coepi.android.api.JniPublicReport(
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
        val n = org.coepi.android.api.NativeApi()
        val value = n.testReturnMultipleAlerts()
        assertEquals(
            org.coepi.android.api.JniAlertsArrayResult(
                1, "", arrayOf(
                    org.coepi.android.api.JniAlert(
                        "123", org.coepi.android.api.JniPublicReport(
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
                    org.coepi.android.api.JniAlert(
                        "343356", org.coepi.android.api.JniPublicReport(
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
