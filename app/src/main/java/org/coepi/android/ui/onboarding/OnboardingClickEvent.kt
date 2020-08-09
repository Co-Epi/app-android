package org.coepi.android.ui.onboarding

sealed class OnboardingClickEvent {
    object NextClicked : OnboardingClickEvent()
    object PrivacyLinkClicked : OnboardingClickEvent()
    object JoinClicked : OnboardingClickEvent()
}
