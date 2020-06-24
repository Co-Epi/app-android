package org.coepi.android.core

import org.coepi.android.common.Result
import org.coepi.android.common.Result.Failure
import org.coepi.android.common.Result.Success
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.UserInput.None
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.tcn.Alert
import org.coepi.android.tcn.toCoughSeverity
import org.coepi.android.tcn.toFeverSeverity

interface AlertsFetcher {
    fun fetchNewAlerts(): Result<List<Alert>, Throwable>
}

class AlertsFetcherImpl(private val api: NativeCore) : AlertsFetcher {

    override fun fetchNewAlerts(): Result<List<Alert>, Throwable> {
        val result = api.fetchNewReports()
        return when (result.status) {
            1 -> Success(result.obj.map { it.toAlert() })
            else -> Failure(Throwable(result.statusDescription()))
        }
    }

    private fun JniAlertsArrayResult.statusDescription(): String =
        statusDescription(status, message)

    private fun statusDescription(status: Int, message: String) =
        "Status: $status Message: $message"

    private fun JniAlert.toAlert() = Alert(
        id = id,
        contactTime = when {
            contactTime < 0 -> error("Invalid contact time: $contactTime")
            else -> UnixTime.fromValue(contactTime)
        },
        reportTime = when {
            report.reportTime < 0 -> error("Invalid report time: ${report.reportTime}")
            else -> UnixTime.fromValue(report.reportTime)
        },
        earliestSymptomTime = when {
            report.earliestSymptomTime == -1L ->
                None
            report.earliestSymptomTime < -1L ->
                error("Invalid earliestSymptomTime: ${report.earliestSymptomTime}")
            else ->
                Some(UnixTime.fromValue(report.earliestSymptomTime))
        },
        feverSeverity = toFeverSeverity(report.feverSeverity),
        coughSeverity = toCoughSeverity(report.coughSeverity),
        breathlessness = report.breathlessness,
        muscleAches = report.muscleAches,
        lossSmellOrTaste = report.lossSmellOrTaste,
        diarrhea = report.diarrhea,
        runnyNose = report.runnyNose,
        other = report.other,
        noSymptoms = report.noSymptoms
    )
}
