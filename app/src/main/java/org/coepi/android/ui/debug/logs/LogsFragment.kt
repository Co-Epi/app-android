package org.coepi.android.ui.debug.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.R.layout.item_min_loglevel
import org.coepi.android.databinding.FragmentLogsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.coepi.android.system.log.LogLevel
import org.coepi.android.ui.extensions.onItemSelected
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogsFragment: Fragment() {

    private val viewModel by viewModel<LogsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        vm = viewModel

        val logsAdapter = LogsRecyclerViewAdapter(onItemLongClick = {
            viewModel.onLogLongTap()
        })

        logsRecyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = logsAdapter
        }

        val logLevelAdapter= ArrayAdapter(inflater.context,
            item_min_loglevel, LogLevel.values().map { it.text }.toTypedArray())
        logLevelAdapter.setDropDownViewResource(item_min_loglevel)
        spinLevel.adapter = logLevelAdapter

        viewModel.selectedLogLevel.observeWith(viewLifecycleOwner) {
            spinLevel.setSelection(it.ordinal, false)
        }

        // Ignore automatic call to onItemSelected when fragment loads
        var firstOnItemSelectedCall = true
        spinLevel.onItemSelected {
            val selected = spinLevel.selectedItem.toString()
            // FIXME using the first letter to get enum value is a hack
            val logLevel = LogLevel.valueOf("${selected[0]}")
            if (!firstOnItemSelectedCall) {
                viewModel.onLogLevelSelected(logLevel)
            }
            firstOnItemSelectedCall = false
        }

        viewModel.logs.observeWith(viewLifecycleOwner) {
            logsAdapter.submitList(it)
        }

    }.root
}
