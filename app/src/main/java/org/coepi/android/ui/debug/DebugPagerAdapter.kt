package org.coepi.android.ui.debug

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.coepi.android.ui.debug.ble.DebugBleFragment
import org.coepi.android.ui.debug.cen.CENFragment
import org.coepi.android.ui.debug.logs.LogsFragment

class DebugPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> DebugBleFragment()
        1 -> LogsFragment()
        2 -> CENFragment()
        else -> error("Not handled: $position")
    }
}
