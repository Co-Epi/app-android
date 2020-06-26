package org.coepi.android.ui.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.databinding.FragmentAlertsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.coepi.android.ui.common.SwipeToDeleteCallback
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlertsFragment : Fragment() {
    private val viewModel by viewModel<AlertsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        toolbar.setNavigationOnClickListener { viewModel.onBack() }

        val alertsAdapter = AlertsAdapter(
            onAlertClick = {
                viewModel.onAlertClick(it)
            },
            onAlertDismissed = {
                viewModel.onAlertDismissed(it)
            }
        )

        // clear system notifications if any are present
        // TODO: will need to clear by unique notification ids
        viewModel.onUiReady()

        recyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = alertsAdapter
        }

        swipeRefresh.setOnRefreshListener {
            viewModel.onSwipeToRefresh()
            swipeRefresh.isRefreshing = false
        }

        context?.let {
            val swipeHandler = object : SwipeToDeleteCallback(alertsAdapter) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = recyclerView.adapter as AlertsAdapter
                    adapter.removeAt(viewHolder.adapterPosition)
                }
            }
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(recyclerView)
        }

        viewModel.alerts.observeWith(viewLifecycleOwner) {
            alertsAdapter.submitList(it)
        }
    }.root
}
