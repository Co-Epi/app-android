package org.coepi.android.ui.debug.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.R
import org.coepi.android.R.drawable.ic_close
import org.coepi.android.databinding.FragmentLogsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.coepi.android.system.log.LogLevel
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
        toolbar.setNavigationIcon(ic_close)
        toolbar.setNavigationOnClickListener {
            viewModel.onCloseClick()
        }

        logsRecyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = logsAdapter
        }

        //Fill spinner with Log Level values
        val logLevelAdapter = ArrayAdapter(inflater.context , R.layout.item_min_loglevel, LogLevel.values().map { value -> value.text }.toTypedArray())
        logLevelAdapter.setDropDownViewResource(R.layout.item_min_loglevel)
        spinLevel.adapter = logLevelAdapter
        spinLevel.setSelection(0)

        spinLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selected = spinLevel.selectedItem.toString()
                val selectedLevel = LogLevel.valueOf("" + selected[0])

                //Filter log messages
                viewModel.logs.observeWith(viewLifecycleOwner) {
                    logsAdapter.setItems(it.filter { entry -> entry.level.compareTo(selectedLevel) >= 0 })
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }.root
}
