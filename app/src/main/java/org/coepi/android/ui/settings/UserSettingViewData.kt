package org.coepi.android.ui.settings

sealed class UserSettingViewData {
    data class SectionHeader(val title: String, val description: String) : UserSettingViewData()
    data class Toggle(
        val text: String, val value: Boolean, val hasBottomLine: Boolean,
        val id: UserSettingToggleId
    ) : UserSettingViewData()
    data class Text(val text: String, val id: UserSettingClickId,
                    val hasBottomLine: Boolean) : UserSettingViewData()
}

enum class UserSettingToggleId {
    FILTER_ALERTS_WITH_SYMPTOMS,
    FILTER_ALERTS_WITH_LONG_DURATION,
//    FILTER_ALERTS_WITH_SHORT_DISTANCE
}

enum class UserSettingClickId {
    PRIVACY_STATEMENT,
    REPORT_PROBLEM,
    APP_VERSION,
}
