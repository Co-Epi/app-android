package org.coepi.android.ui.onboarding

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.coepi.android.R.drawable
import org.coepi.android.databinding.CardOnboardingLargeBinding
import org.coepi.android.databinding.CardOnboardingSmallBinding
import org.coepi.android.ui.onboarding.OnboardingCardViewData.LargeCard
import org.coepi.android.ui.onboarding.OnboardingCardViewData.SmallCard
import org.coepi.android.ui.onboarding.OnboardingClickEvent.FaqClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.JoinClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.NextClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.PrivacyLinkClicked

class OnboardingAdapter(
    private val data: List<OnboardingCardViewData>,
    private val onEvent: (OnboardingClickEvent) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            0 -> OnboardingSmallCardViewHolder(parent)
            1 -> OnboardingLargeCardViewHolder(parent)
            else -> error("Not handled: $viewType")
        }

    override fun getItemViewType(position: Int): Int = when (data[position]) {
        is SmallCard -> 0
        is LargeCard -> 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = data[position]) {
            is SmallCard -> (holder as OnboardingSmallCardViewHolder).bind(item, onEvent)
            is LargeCard -> (holder as OnboardingLargeCardViewHolder).bind(item, onEvent)
        }
    }

    override fun getItemCount(): Int = data.size
}

class OnboardingSmallCardViewHolder(
    private val parent: ViewGroup,
    private val binding: CardOnboardingSmallBinding =
        CardOnboardingSmallBinding.inflate(from(parent.context), parent, false))
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(viewData: SmallCard, onEvent: (OnboardingClickEvent) -> Unit): Unit = binding.run {
        this.viewData = viewData

        when (viewData.highlightedDot) {
            0 -> progressIcon1
            1 -> progressIcon2
            2 -> progressIcon3
            else -> error("Not handled: ${viewData.highlightedDot}")
        }.setImageResource(drawable.stepper_icon_selected)

        dataUsageLinkLabel.setOnClickListener {
            onEvent(PrivacyLinkClicked)
        }
        onboardingCardSmallNextButton.setOnClickListener {
            onEvent(NextClicked)
        }
    }
}

class OnboardingLargeCardViewHolder(
    private val parent: ViewGroup,
    private val binding: CardOnboardingLargeBinding =
        CardOnboardingLargeBinding.inflate(from(parent.context), parent, false))
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(viewData: LargeCard, onEvent: (OnboardingClickEvent) -> Unit): Unit = binding.run {
        this.viewData = viewData

        onboardingCardLargeJoinButton.setOnClickListener {
            onEvent(JoinClicked)
        }
        onboardingCardLargeFaqButton.setOnClickListener {
            onEvent(FaqClicked)
        }
        dataUsageLinkLabel.setOnClickListener {
            onEvent(PrivacyLinkClicked)
        }
    }
}
