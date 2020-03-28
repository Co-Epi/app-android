package org.coepi.android.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.coepi.android.databinding.FragmentHomeBinding.inflate
import kotlinx.android.synthetic.main.fragment_home.*

import org.coepi.android.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    // TODO: Refactor to use list, see Symptoms

    private val viewModel by viewModel<HomeViewModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        check_in.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToCareFragment()

            findNavController().navigate(action)
        }

        see_alerts.setOnClickListener {
            // TODO: call nav action to contact alert fragment
        }
    }

}
