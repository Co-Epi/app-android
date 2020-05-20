package org.coepi.android.ui.onboarding


sealed class OnboardingCard(val title: String, val message: CharSequence) {

    class SmallOnboardingCard(
        title: String,
        message: CharSequence,
        val order: Int
    ): OnboardingCard(title, message)

    class LargeOnboardingCard(
        title: String,
        message: CharSequence
    ): OnboardingCard(title, message)

}
