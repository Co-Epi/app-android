package org.coepi.android.ui.alertsinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.coepi.android.databinding.FragmentExposureAlertsInformationBinding.inflate
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlertsInfoFragment : Fragment() {
    private val viewModel by viewModel<AlertsInfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel
        toolbar.setNavigationOnClickListener { viewModel.onBack() }
    }.root
    
}
