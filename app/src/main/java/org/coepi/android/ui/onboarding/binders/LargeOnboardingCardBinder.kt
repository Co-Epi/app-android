package org.coepi.android.ui.onboarding.binders

import android.view.View
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.card_onboarding_large.view.*
import org.coepi.android.ui.onboarding.OnboardingCard
import org.coepi.android.ui.onboarding.OnboardingCard.LargeOnboardingCard
import org.coepi.android.ui.onboarding.OnboardingClickEvent

object LargeOnboardingCardBinder: OnboardingCardBinder {

    override fun bind(
        itemView: View,
        onboardingCard: OnboardingCard,
        onboardingEventSubject: Subject<OnboardingClickEvent>
    ) {
        onboardingCard as LargeOnboardingCard

        itemView.onboarding_card_large_title.text = onboardingCard.title
        itemView.onboarding_card_large_content.text = onboardingCard.message

        itemView.onboarding_card_large_join_button.setOnClickListener {
            onboardingEventSubject.onNext(OnboardingClickEvent.JoinClicked)
        }

        itemView.onboarding_card_large_faq_button.setOnClickListener {
            onboardingEventSubject.onNext(OnboardingClickEvent.FaqClicked)
        }

        itemView.data_usage_link_label.setOnClickListener {
            onboardingEventSubject.onNext(OnboardingClickEvent.PrivacyLinkClicked)
        }

    }
}
