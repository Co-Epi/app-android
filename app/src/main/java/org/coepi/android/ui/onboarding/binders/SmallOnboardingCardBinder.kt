package org.coepi.android.ui.onboarding.binders

import android.view.View
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.card_onboarding_small.view.*
import org.coepi.android.R
import org.coepi.android.ui.onboarding.OnboardingCard
import org.coepi.android.ui.onboarding.OnboardingCard.SmallOnboardingCard
import org.coepi.android.ui.onboarding.OnboardingClickEvent

object SmallOnboardingCardBinder: OnboardingCardBinder {

    override fun bind(
        itemView: View,
        onboardingCard: OnboardingCard,
        onboardingEventSubject: Subject<OnboardingClickEvent>
    ) {
        onboardingCard as SmallOnboardingCard

        itemView.onboarding_card_small_title.text = onboardingCard.title
        itemView.onboarding_card_small_content.text = onboardingCard.message

        when (onboardingCard.order) {
            0 -> itemView.progress_icon_1.setImageResource(R.drawable.stepper_icon_selected)
            1 -> itemView.progress_icon_2.setImageResource(R.drawable.stepper_icon_selected)
            2 -> itemView.progress_icon_3.setImageResource(R.drawable.stepper_icon_selected)
        }

        itemView.data_usage_link_label.setOnClickListener {
            onboardingEventSubject.onNext(OnboardingClickEvent.privacyLinkClicked())
        }

        itemView.onboarding_card_small_next_button.setOnClickListener {
            onboardingEventSubject.onNext(OnboardingClickEvent.nextClicked(onboardingCard.order))
        }
    }
}
