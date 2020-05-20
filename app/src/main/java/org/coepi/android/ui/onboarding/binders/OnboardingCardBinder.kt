package org.coepi.android.ui.onboarding.binders

import android.view.View
import io.reactivex.subjects.Subject
import org.coepi.android.ui.onboarding.OnboardingCard
import org.coepi.android.ui.onboarding.OnboardingClickEvent

interface OnboardingCardBinder {
    fun bind(itemView: View,
             onboardingCard: OnboardingCard,
             onboardingEventSubject: Subject<OnboardingClickEvent>
    )
}
