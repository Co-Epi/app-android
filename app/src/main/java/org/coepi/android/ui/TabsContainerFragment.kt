package org.coepi.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.coepi.android.R
import org.coepi.android.R.id.bottom_navigation
import org.coepi.android.R.id.navHostFragment

class TabsContainerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_tabs_container, container, false).also {
        setupBottomNavigation(it)
    }

    private fun setupBottomNavigation(view: View) {
        val bottomNavigationView = view.findViewById<BottomNavigationView>(bottom_navigation)
        val fragmentContainer = view.findViewById<View>(navHostFragment)
        bottomNavigationView.setupWithNavController(Navigation.findNavController(fragmentContainer))
    }
}
