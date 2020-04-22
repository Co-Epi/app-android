package org.coepi.android.ui.debug

import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import org.coepi.android.R.drawable.ic_close
import org.coepi.android.databinding.FragmentDebugBinding.inflate
import org.koin.androidx.viewmodel.ext.android.viewModel

class DebugFragment: Fragment() {

    private val viewModel by viewModel<DebugViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        vm = viewModel

        toolbar.apply {
            setNavigationIcon(ic_close)
            setNavigationOnClickListener {
                viewModel.onCloseClick()
            }
            navigationIcon?.mutate()?.let {
                it.setTint(WHITE)
                navigationIcon = it
            }
        }

        val pagerAdapter = DebugPagerAdapter(this@DebugFragment)
        pager.adapter = pagerAdapter

        TabLayoutMediator(tabs, pager,
            TabConfigurationStrategy { tab: Tab, position: Int -> when (position) {
                0 -> tab.text = "BLE"
                1 -> tab.text = "Logs"
            }}
        ).attach()
    }.root
}
