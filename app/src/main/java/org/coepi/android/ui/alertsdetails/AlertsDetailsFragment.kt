package org.coepi.android.ui.alertsdetails

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import kotlinx.android.parcel.Parcelize
import org.coepi.android.R.id.alert_details_menu_delete
import org.coepi.android.R.id.alert_details_menu_report
import org.coepi.android.R.menu.alert_details
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel
        activity?.let {
            toolbar.configure(it)
        }
    }.root

    private fun Toolbar.configure(activity: Activity) {
        setNavigationOnClickListener { viewModel.onBack() }
        inflateMenu(alert_details)
        setOnMenuItemClickListener { item ->
            when (item.itemId) {
                alert_details_menu_delete -> viewModel.onDeleteTap()
                alert_details_menu_report -> viewModel.onReportProblemTap(activity)
                else -> error("Not handled menu item: $item")
            }
            true
        }
    }

    @Parcelize
    data class Args(val alert: Alert) : Parcelable
}
