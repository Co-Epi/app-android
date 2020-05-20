package org.coepi.android.ui.onboarding

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_onboarding.*
import org.coepi.android.R
import org.coepi.android.R.string
import org.coepi.android.databinding.FragmentOnboardingBinding.inflate
import org.coepi.android.ui.onboarding.OnboardingCard.LargeOnboardingCard
import org.coepi.android.ui.onboarding.OnboardingCard.SmallOnboardingCard
import org.coepi.android.ui.onboarding.OnboardingClickEvent.faqClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.joinClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.nextClicked
import org.coepi.android.ui.onboarding.OnboardingClickEvent.privacyLinkClicked
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment : Fragment() {

    private val viewModel by viewModel<OnboardingViewModel>()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        onboarding_info_recycler.layoutManager =
            object :LinearLayoutManager(context, HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean = false
            }

        onboarding_info_recycler.adapter =
            OnboardingAdapter(
                initializeOnboardingCards(requireContext())
            ).apply {
                onboardingEventStream
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (it) {
                            is nextClicked ->
                                onboarding_info_recycler.layoutManager?.scrollToPosition(it.currentPosition + 1)

                            is privacyLinkClicked -> startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_WEBPAGE_URL))
                            )

                            is joinClicked -> viewModel.onCloseClick()

                            is faqClicked -> startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse(FAQ_WEBPAGE_URL)
                                )
                            )
                        }
                    }.addTo(compositeDisposable)
            }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun initializeOnboardingCards(context: Context): List<OnboardingCard> = listOf(
        SmallOnboardingCard(
            context.resources.getString(string.onboarding_card_title_1),
            context.resources.getString(string.onboarding_card_content_1),
            0
        ),
        SmallOnboardingCard(
            context.resources.getString(string.onboarding_card_title_2),
            context.resources.getString(string.onboarding_card_content_2),
            1
        ),
        SmallOnboardingCard(
            context.resources.getString(string.onboarding_card_title_3),
            context.resources.getString(string.onboarding_card_content_3),
            2
        ),
        LargeOnboardingCard(
            context.resources.getString(string.onboarding_card_title_4),
            context.resources.getString(string.onboarding_card_content_4)
        )
    )

    companion object {

        private const val PRIVACY_WEBPAGE_URL = "https://www.coepi.org/privacy/"
        private const val FAQ_WEBPAGE_URL = "https://www.coepi.org/faq/"

        const val SMALL_CARD = 0
        const val LARGE_CARD = 1
    }
}
