package org.coepi.android.ui.debug

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.coepi.android.ui.debug.ble.DebugBleFragment
import org.coepi.android.ui.debug.logs.LogsFragment

class DebugPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> DebugBleFragment()
        1 -> LogsFragment()
        else -> error("Not handled: $position")
    }
}
