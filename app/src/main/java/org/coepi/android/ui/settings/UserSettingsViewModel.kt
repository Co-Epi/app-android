package org.coepi.android.ui.settings

import android.app.Activity
import android.net.Uri
import android.net.Uri.parse
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.PublishSubject.create
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.coepi.android.R
import org.coepi.android.R.string.privacy_link
import org.coepi.android.R.string.user_settings_item_distance_shorter_than
import org.coepi.android.R.string.user_settings_item_duration_longer_than
import org.coepi.android.R.string.user_settings_item_privacy_statement
import org.coepi.android.R.string.user_settings_item_report_problem
import org.coepi.android.R.string.user_settings_item_show_all
import org.coepi.android.R.string.user_settings_section_alerts_subtitle
import org.coepi.android.R.string.user_settings_section_alerts_title
import org.coepi.android.domain.model.LengthMeasurementUnit
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertFilterSettings
import org.coepi.android.system.Email
import org.coepi.android.system.EnvInfos
import org.coepi.android.system.Resources
import org.coepi.android.system.UnitSystemProvider
import org.coepi.android.system.WebLaunchEventEmitter
import org.coepi.android.system.rx.ObservablePreferences
import org.coepi.android.ui.formatters.MeasurementFormatter
import org.coepi.android.ui.settings.UserSettingClickId.APP_VERSION
import org.coepi.android.ui.settings.UserSettingClickId.PRIVACY_STATEMENT
import org.coepi.android.ui.settings.UserSettingClickId.REPORT_PROBLEM
import org.coepi.android.ui.settings.UserSettingToggleId.FILTER_ALERTS_WITH_LONG_DURATION
import org.coepi.android.ui.settings.UserSettingToggleId.FILTER_ALERTS_WITH_SHORT_DISTANCE
import org.coepi.android.ui.settings.UserSettingToggleId.FILTER_ALERTS_WITH_SYMPTOMS
import org.coepi.android.ui.settings.UserSettingViewData.SectionHeader
import org.coepi.android.ui.settings.UserSettingViewData.Text
import org.coepi.android.ui.settings.UserSettingViewData.Toggle

class UserSettingsViewModel(
    private val preferences: ObservablePreferences,
    private val email: Email,
    private val measurementFormatter: MeasurementFormatter,
    private val resources: Resources,
    private val envInfos: EnvInfos,
    unitsProvider: UnitSystemProvider,
    private val alertFilterSettings: AlertFilterSettings,
    private val webLaunchEventEmitter: WebLaunchEventEmitter
) : ViewModel() {

    val settings: LiveData<List<UserSettingViewData>> =
        Observables.combineLatest(
            preferences.filterAlertsWithSymptoms,
            preferences.filterAlertsWithLongDuration,
            preferences.filterAlertsWithShortDistance,
            unitsProvider.measureUnit,
            { a, b, c, d -> BuildSettingsPars(a, b, c, d) }
        )
            .map { pars ->
                buildSettings(
                    pars.filterAlertsWithSymptoms,
                    pars.filterAlertsWithLongDuration,
                    pars.filterAlertsWithShortDistance, alertFilterSettings,
                    envInfos.appVersionString(),
                    pars.measureUnit
                )
            }
            // Don't update the UI on switch change: the toggle is already updated
            // and the no-op update interferes with the animation
            .distinct { (a, b) ->
                if (a is Toggle && b is Toggle) {
                    // Detect changes in fields other than value
                    a.copy(value = true) != b.copy(value = true)
                } else {
                    true
                }
            }
            .toLiveData()

    private val showWebSubject = create<Uri>()
    val showWeb = showWebSubject.toLiveData()

    fun onToggle(item: Toggle, value: Boolean) {
        when (item.id) {
            FILTER_ALERTS_WITH_SYMPTOMS ->
                // The text says "show all reports" -> negate for filter
                preferences.setFilterAlertsWithSymptoms(!value)
            FILTER_ALERTS_WITH_SHORT_DISTANCE ->
                preferences.setFilterAlertsWithShortDistance(value)
            FILTER_ALERTS_WITH_LONG_DURATION ->
                preferences.setFilterAlertsWithLongDuration(value)
        }
    }

    fun onClick(item: Text, activity: Activity) {
        when (item.id) {
            REPORT_PROBLEM ->
                email.open(activity, "TODO@TODO.TODO", "TODO")
            PRIVACY_STATEMENT ->
                webLaunchEventEmitter.launch(parse(resources.getString(privacy_link)))
            else -> {}
        }
    }

    private fun buildSettings(
        filterAlertsWithSymptoms: Boolean,
        filterAlertsWithLongDuration: Boolean,
        filterAlertsWithShortDistance: Boolean,
        alertFilterSettings: AlertFilterSettings,
        appVersionString: String,
        measurementUnit: LengthMeasurementUnit
    ): List<UserSettingViewData> = listOf(
        SectionHeader(
            title = resources.getString(user_settings_section_alerts_title),
            description = resources.getString(user_settings_section_alerts_subtitle)
        ),
        Toggle(
            text = resources.getString(user_settings_item_show_all),
            value = !filterAlertsWithSymptoms,
            id = FILTER_ALERTS_WITH_SYMPTOMS,
            hasBottomLine = true
        ),
        Toggle(
            text = resources.getString(
                user_settings_item_duration_longer_than,
                alertFilterSettings.durationSecondsLargerThan / 60
            ),
            value = filterAlertsWithLongDuration,
            id = FILTER_ALERTS_WITH_LONG_DURATION,
            hasBottomLine = true
        ),
        Toggle(
            text = resources.getString(
                user_settings_item_distance_shorter_than,
                measurementFormatter.format(
                    alertFilterSettings.distanceShorterThan
                        .to(measurementUnit)
                )
            ),
            value = filterAlertsWithShortDistance,
            id = FILTER_ALERTS_WITH_SHORT_DISTANCE,
            hasBottomLine = false
        ),
        Text(
            text = resources.getString(user_settings_item_privacy_statement),
            id = PRIVACY_STATEMENT,
            hasBottomLine = true
        ),
        Text(
            text = resources.getString(user_settings_item_report_problem),
            id = REPORT_PROBLEM,
            hasBottomLine = true
        ),
        Text(text = appVersionString, id = APP_VERSION, hasBottomLine = false)
    )

    private fun EnvInfos.appVersionString() = "$appVersionName ($appVersionCode)"
}

private data class BuildSettingsPars(
    val filterAlertsWithSymptoms: Boolean,
    val filterAlertsWithLongDuration: Boolean,
    val filterAlertsWithShortDistance: Boolean,
    val measureUnit: LengthMeasurementUnit
)
