package org.coepi.android.ui.debug

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.coepi.android.ui.cen.CENFragment
import org.coepi.android.ui.debug.logs.LogsFragment

class DebugPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> LogsFragment()
        1 -> CENFragment()
        else -> error("Not handled: $position")
    }
}
