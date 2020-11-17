package org.coepi.android.ui.settings

import android.app.Activity
import android.net.Uri
import android.net.Uri.parse
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.PublishSubject.create
import org.coepi.android.R.string.privacy_link
import org.coepi.android.R.string.user_settings_item_duration_longer_than
import org.coepi.android.R.string.user_settings_item_privacy_statement
import org.coepi.android.R.string.user_settings_item_report_problem
import org.coepi.android.R.string.user_settings_item_show_all
import org.coepi.android.R.string.user_settings_section_alerts_subtitle
import org.coepi.android.R.string.user_settings_section_alerts_title
import org.coepi.android.extensions.rx.toLiveData
import org.coepi.android.repo.AlertFilterSettings
import org.coepi.android.system.Email
import org.coepi.android.system.EnvInfos
import org.coepi.android.system.Resources
import org.coepi.android.system.UnitsProvider
import org.coepi.android.system.WebLaunchEventEmitter
import org.coepi.android.system.rx.ObservablePreferences
import org.coepi.android.ui.settings.UserSettingClickId.APP_VERSION
import org.coepi.android.ui.settings.UserSettingClickId.PRIVACY_STATEMENT
import org.coepi.android.ui.settings.UserSettingClickId.REPORT_PROBLEM
import org.coepi.android.ui.settings.UserSettingToggleId.FILTER_ALERTS_WITH_LONG_DURATION
import org.coepi.android.ui.settings.UserSettingToggleId.FILTER_ALERTS_WITH_SYMPTOMS
import org.coepi.android.ui.settings.UserSettingViewData.SectionHeader
import org.coepi.android.ui.settings.UserSettingViewData.Text
import org.coepi.android.ui.settings.UserSettingViewData.Toggle
import org.coepi.core.domain.model.LengthtUnit

class UserSettingsViewModel(
    private val preferences: ObservablePreferences,
    private val email: Email,
    private val resources: Resources,
    private val envInfos: EnvInfos,
    unitsProvider: UnitsProvider,
    private val alertFilterSettings: AlertFilterSettings,
    private val webLaunchEventEmitter: WebLaunchEventEmitter
) : ViewModel() {

    val settings: LiveData<List<UserSettingViewData>> =
        Observables.combineLatest(
            preferences.filterAlertsWithSymptoms,
            preferences.filterAlertsWithLongDuration,
            preferences.filterAlertsWithShortDistance,
            unitsProvider.lengthUnit,
            { a, b, c, d -> BuildSettingsPars(a, b, c, d) }
        )
            .map { pars ->
                buildSettings(
                    pars.filterAlertsWithSymptoms,
                    pars.filterAlertsWithLongDuration,
                    alertFilterSettings,
                    envInfos.appVersionString()
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
            FILTER_ALERTS_WITH_LONG_DURATION ->
                preferences.setFilterAlertsWithLongDuration(value)
        }
    }

    fun onClick(item: Text, activity: Activity, uri: Uri?) {
        when (item.id) {
            REPORT_PROBLEM ->
                email.open(activity, "duskoetf@gmail.com", "Problem with CoEpi", "",  uri)
            PRIVACY_STATEMENT ->
                webLaunchEventEmitter.launch(parse(resources.getString(privacy_link)))
            else -> {}
        }
    }

    private fun buildSettings(
        filterAlertsWithSymptoms: Boolean,
        filterAlertsWithLongDuration: Boolean,
        alertFilterSettings: AlertFilterSettings,
        appVersionString: String
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
    val measureUnit: LengthtUnit
)
