package org.coepi.android.ui.debug.logs

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

class LogsFragment: Fragment() {
    private val viewModel by viewModel<LogsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {

        toolbar.setNavigationIcon(ic_close)
        toolbar.setNavigationOnClickListener {
            viewModel.onCloseClick()
        }

        val logsAdapter = LogsRecyclerViewAdapter()

        logsRecyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = logsAdapter
        }

        viewModel.logs.observeWith(viewLifecycleOwner) {
            logsAdapter.setItems(it)
        }
    }.root
}
