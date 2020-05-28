package org.coepi.android.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.databinding.FragmentAlertsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlertsFragment: Fragment() {
    private val viewModel by viewModel<AlertsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        val alertsAdapter = AlertsAdapter(onAckClick = {
            viewModel.onAlertAckClick(it)
        }, onAlertClick = {
            viewModel.onAlertClick(it)
        })

        recyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = alertsAdapter
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.onSwipeToRefresh()
            swipeRefresh.isRefreshing = false
        }

        viewModel.alerts.observeWith(viewLifecycleOwner) {
            alertsAdapter.submitList(it)
        }
    }.root
}
