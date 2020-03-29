package org.coepi.android.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.R.drawable.ic_close
import org.coepi.android.databinding.FragmentLogsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationsFragment: Fragment() {
    private val viewModel by viewModel<NotificationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {

        toolbar.setNavigationIcon(ic_close)
        toolbar.setNavigationOnClickListener {
            viewModel.onCloseClick()
        }

        val logsAdapter = NotificationViewAdapter()

        logsRecyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = logsAdapter
        }

        viewModel.symptoms.observeWith(viewLifecycleOwner) {
            logsAdapter.setItems(it)
        }
    }.root
}
