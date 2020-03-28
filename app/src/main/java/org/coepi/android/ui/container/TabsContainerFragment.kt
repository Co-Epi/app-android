package org.coepi.android.ui.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
//import org.coepi.android.R.id.bottom_navigation
import org.coepi.android.R.id.navHostFragment
import org.coepi.android.databinding.FragmentTabsContainerBinding.inflate
import org.coepi.android.ui.onboarding.OnboardingViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TabsContainerFragment : Fragment() {
    private val viewModel by viewModel<TabsContainerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

    }.root
}
