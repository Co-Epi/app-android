package org.coepi.android.ui.onboarding

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.coepi.android.databinding.FragmentOnboardingBinding.inflate
import org.coepi.android.extensions.observeWith
import org.koin.androidx.viewmodel.ext.android.viewModel

class OnboardingFragment : Fragment() {
    private val viewModel by viewModel<OnboardingViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        onboardingInfoRecycler.layoutManager =
            object : LinearLayoutManager(context, HORIZONTAL, false) {
                override fun canScrollHorizontally(): Boolean = false
            }

        onboardingInfoRecycler.adapter = OnboardingAdapter(
            viewModel.viewData,
            onEvent = viewModel::onCardEvent
        )

        viewModel.openLink.observeWith(viewLifecycleOwner) { uri ->
            startActivity(Intent(ACTION_VIEW, uri))
        }

        lifecycleOwner?.let {
            activity?.onBackPressedDispatcher?.addCallback(
                it,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        viewModel.onBack()
                    }
                })
        }

        viewModel.recyclerViewScrollPosition.observeWith(viewLifecycleOwner) { position ->
            if (position < 0) {
                // Case where user presses back and they are on the first onboarding tab
                activity?.finish()
            } else {
                onboardingInfoRecycler.scrollToPosition(position)
            }
        }
    }.root
}
