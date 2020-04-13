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
    private val logsAdapter = LogsRecyclerViewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        vm = viewModel

        logsRecyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = logsAdapter
        }

        val logLevelAdapter= ArrayAdapter(inflater.context,
            item_min_loglevel, LogLevel.values().map { it.text }.toTypedArray())
        logLevelAdapter.setDropDownViewResource(item_min_loglevel)
        spinLevel.adapter = logLevelAdapter
        spinLevel.setSelection(0)

        spinLevel.onItemSelected {
            val selected = spinLevel.selectedItem.toString()
            val logLevel = LogLevel.valueOf("${selected[0]}") // FIXME using the first letter to get enum value is a hack
            viewModel.onLogLevelSelected(logLevel)
        }

        viewModel.logs.observeWith(viewLifecycleOwner) {
            logsAdapter.setItems(it)
        }

    }.root
}
