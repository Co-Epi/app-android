package org.coepi.android.ui.onboarding

sealed class OnboardingClickEvent {

    class nextClicked(val currentPosition: Int) : OnboardingClickEvent()
    class privacyLinkClicked() : OnboardingClickEvent()
    class joinClicked() : OnboardingClickEvent()
    class faqClicked() : OnboardingClickEvent()

}
