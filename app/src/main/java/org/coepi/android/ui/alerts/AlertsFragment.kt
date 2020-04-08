package org.coepi.android.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.R.drawable.ic_close
import org.coepi.android.databinding.FragmentAlertsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.coepi.android.ui.common.NotificationsObserver
import org.coepi.android.ui.common.ProgressObserver
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date

class AlertsFragment: Fragment() {
    private val viewModel by viewModel<AlertsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        viewModel.notification.observe(viewLifecycleOwner, NotificationsObserver(activity))
        viewModel.isInProgress.observe(viewLifecycleOwner, ProgressObserver(requireContext()))

        val alertsAdapter = AlertsAdapter(onItemClick = {
            viewModel.onAlertClick(it)
        })

        recyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = alertsAdapter
        }

        viewModel.alerts.observeWith(viewLifecycleOwner) {
            alertsAdapter.submitList(it)
        }
    }.root
}
