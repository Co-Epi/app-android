package org.coepi.android.ui.onboarding

sealed class OnboardingCardViewData(val title: String, val message: CharSequence) {
    class SmallCard(title: String, message: CharSequence, val highlightedDot: Int)
        : OnboardingCardViewData(title, message)

    class LargeCard(title: String, message: CharSequence)
        : OnboardingCardViewData(title, message)
}
