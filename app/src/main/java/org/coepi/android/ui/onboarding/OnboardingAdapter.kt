package org.coepi.android.ui.onboarding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import org.coepi.android.R
import org.coepi.android.ui.onboarding.OnboardingCard.LargeOnboardingCard
import org.coepi.android.ui.onboarding.OnboardingCard.SmallOnboardingCard
import org.coepi.android.ui.onboarding.OnboardingFragment.Companion.LARGE_CARD
import org.coepi.android.ui.onboarding.OnboardingFragment.Companion.SMALL_CARD
import org.coepi.android.ui.onboarding.binders.LargeOnboardingCardBinder
import org.coepi.android.ui.onboarding.binders.SmallOnboardingCardBinder

class OnboardingAdapter(private val data: List<OnboardingCard>) :
    RecyclerView.Adapter<OnboardingViewHolder>() {

    val onboardingEventStream: Observable<OnboardingClickEvent>
        get() = onboardingEventSubject

    private val onboardingEventSubject = PublishSubject.create<OnboardingClickEvent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        OnboardingViewHolder(
        when (viewType) {
            SMALL_CARD -> LayoutInflater.from(parent.context).inflate(R.layout.card_onboarding_small, parent, false)
            LARGE_CARD -> LayoutInflater.from(parent.context).inflate(R.layout.card_onboarding_large, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.card_onboarding_unknown, parent, false)
        },
        onboardingEventSubject
    )


    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int =
        when (data[position]) {
            is SmallOnboardingCard -> SMALL_CARD
            is LargeOnboardingCard -> LARGE_CARD
        }

}

class OnboardingViewHolder(
    view: View,
    private val onboardingEventSubject: Subject<OnboardingClickEvent>
) : RecyclerView.ViewHolder(view) {

    fun bind(onboardingCard: OnboardingCard) {
        when (onboardingCard) {
            is SmallOnboardingCard -> SmallOnboardingCardBinder
            is LargeOnboardingCard -> LargeOnboardingCardBinder
        }.bind(itemView, onboardingCard, onboardingEventSubject)
    }
}
