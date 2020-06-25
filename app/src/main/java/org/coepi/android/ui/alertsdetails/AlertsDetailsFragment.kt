package org.coepi.android.ui.alertsdetails

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import org.coepi.android.databinding.FragmentAlertsDetailsBinding.inflate
import org.coepi.android.ui.alertsdetails.AlertsDetailsFragmentArgs.Companion.fromBundle
import org.coepi.core.domain.model.Alert
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AlertsDetailsFragment : Fragment() {
    private val viewModel by viewModel<AlertsDetailsViewModel> {
        parametersOf(arguments?.let { fromBundle(it) }?.args)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel
        toolbar.setNavigationOnClickListener { viewModel.onBack() }
    }.root

    @Parcelize
    data class Args(val alert: Alert) : Parcelable
}
