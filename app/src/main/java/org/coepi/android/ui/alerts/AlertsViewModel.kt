package org.coepi.android.ui.alerts

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import org.coepi.android.R.string.alerts_report_breathlessness
import org.coepi.android.R.string.alerts_report_cough
import org.coepi.android.R.string.alerts_report_cough_dry
import org.coepi.android.R.string.alerts_report_cough_wet
import org.coepi.android.R.string.alerts_report_fever_mild
import org.coepi.android.R.string.alerts_report_fever_serious
import org.coepi.android.api.publicreport.CoughSeverity
import org.coepi.android.api.publicreport.CoughSeverity.DRY
import org.coepi.android.api.publicreport.CoughSeverity.EXISTING
import org.coepi.android.api.publicreport.CoughSeverity.WET
import org.coepi.android.api.publicreport.FeverSeverity
import org.coepi.android.api.publicreport.FeverSeverity.MILD
import org.coepi.android.api.publicreport.FeverSeverity.SERIOUS
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomInputs.LossSmellOrTaste
import org.coepi.android.domain.symptomflow.SymptomInputs.MuscleAches
import org.coepi.android.domain.symptomflow.UserInput.Some
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertsRepo
import org.coepi.android.system.Resources
import org.coepi.android.system.rx.OperationState.Failure
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.OperationState.Success
import org.coepi.android.system.rx.VoidOperationState
import org.coepi.android.tcn.Alert
import org.coepi.android.ui.alerts.AlertCellViewData.Header
import org.coepi.android.ui.alerts.AlertCellViewData.Item
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsDetails
import org.coepi.android.ui.alerts.AlertsFragmentDirections.Companion.actionGlobalAlertsInfo
import org.coepi.android.ui.alertsdetails.AlertsDetailsFragment.Args
import org.coepi.android.ui.formatters.DateFormatters.hourMinuteFormatter
import org.coepi.android.ui.formatters.DateFormatters.monthDayFormatter
import org.coepi.android.ui.navigation.NavigationCommand.Back
import org.coepi.android.ui.navigation.NavigationCommand.ToDirections
import org.coepi.android.ui.navigation.RootNavigation

class AlertsViewModel(
    val alertsRepo: AlertsRepo,
    private val resources: Resources,
    private val navigation: RootNavigation
) : ViewModel() {

    val alerts: LiveData<List<AlertCellViewData>> = alertsRepo.alerts
        .map { it.toCellViewData() }
        .observeOn(mainThread())
        .toLiveData()

    val updateStatusText: LiveData<String> = alertsRepo.updateReportsState
        .map { toUpdateStatusText(it) }
        .observeOn(mainThread())
        .toLiveData()

    fun onAlertClick(alert: AlertViewData) {
        navigation.navigate(ToDirections(actionGlobalAlertsDetails(Args(alert.alert))))
    }

    fun onSwipeToRefresh() {
        alertsRepo.updateReports()
    }

    fun onBack() {
        navigation.navigate(Back)
    }

    fun onAlertsInfoButtonClick() {
        navigation.navigate(ToDirections(actionGlobalAlertsInfo()))
    }

    /**
     * This function parses [Alert] objects received from the [AlertsRepo] into readable strings that get displayed
     * in the recycler view item_alert views bound by the [AlertsAdapter]
     */
    private fun Alert.toViewData(): AlertViewData =
        AlertViewData(
            exposureType = listOfNotNull(
                report.coughSeverity.toUIString(),
                toBreathlessnessString(report.breathlessness),
                report.feverSeverity.toUIString(),
                toMuscleAcheString(report.muscleAches),
                toLossSmellOrTasteString(report.lossSmellOrTaste),
                toDiarreaString(report.diarrhea),
                toRunnyNoseString(report.runnyNose),
                toOtherString(report.other)
            ).joinToString("\n"),
            contactTime = hourMinuteFormatter.formatTime(contactTime.toDate()),
            contactTimeMonth = monthDayFormatter.formatMonthDay(contactTime.toDate()),
            alert = this
        )

    private fun List<Alert>.toCellViewData(): List<AlertCellViewData> =
        sortedWith(compareByDescending { it.contactTime.value })
            .groupBy { monthDayFormatter.formatMonthDay(it.contactTime.toDate()) }
            .flatMap { entry ->
                listOf(Header(entry.key)) + entry.value.map { alert ->
                    Item(alert.toViewData())
                }
            }

    private fun toBreathlessnessString(breathless: Boolean): String? =
        if (breathless) resources.getString(alerts_report_breathlessness) else null

    private fun toMuscleAcheString(muscleAches: Boolean) : String? =
        if(muscleAches) "Muscle Aches" else null

    private fun toLossSmellOrTasteString(lossSmellOrTaste: Boolean) : String? =
        if(lossSmellOrTaste) "Loss of Smell or Taste" else null

    private fun toDiarreaString(diarrhea: Boolean) : String? =
        if(diarrhea) "Diarrhea" else null

    private fun toRunnyNoseString(runnyNose: Boolean) : String? =
        if(runnyNose) "Runny Nose" else null

    private fun toOtherString(other: Boolean) : String? =
        if(other) "Other" else null

    private fun FeverSeverity.toUIString(): String? =
        when (this) {
            FeverSeverity.NONE -> null
            MILD -> resources.getString(alerts_report_fever_mild)
            SERIOUS -> resources.getString(alerts_report_fever_serious)
        }

    @SuppressLint("DefaultLocale")
    private fun CoughSeverity.toUIString(): String? =
        when (this) {
            CoughSeverity.NONE -> null
            EXISTING -> resources.getString(alerts_report_cough)
            WET -> resources.getString(alerts_report_cough_wet)
            DRY -> resources.getString(alerts_report_cough_dry)
        }

    private fun toUpdateStatusText(operationState: VoidOperationState): String =
        when (operationState) {
            is NotStarted, is Success -> ""
            is Progress -> "Updating..."
            is Failure -> "Error updating: ${operationState.t}"
        }
}
