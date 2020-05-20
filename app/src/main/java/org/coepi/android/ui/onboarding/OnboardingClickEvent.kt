package org.coepi.android.ui.onboarding

sealed class OnboardingClickEvent {

    class NextClicked(val currentPosition: Int) : OnboardingClickEvent()
    object PrivacyLinkClicked : OnboardingClickEvent()
    object JoinClicked : OnboardingClickEvent()
    object FaqClicked : OnboardingClickEvent()

}
