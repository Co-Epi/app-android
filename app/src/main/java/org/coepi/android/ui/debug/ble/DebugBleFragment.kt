package org.coepi.android.ui.debug.ble

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.databinding.FragmentDebugBleBinding.inflate
import org.coepi.android.extensions.observeWith
import org.koin.androidx.viewmodel.ext.android.viewModel

class DebugBleFragment: Fragment() {

    private val viewModel by viewModel<DebugBleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        vm = viewModel

        val bleAdapter = DebugBleRecyclerAdapter()

        logsRecyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = bleAdapter
        }

        viewModel.items.observeWith(viewLifecycleOwner) {
            bleAdapter.submitList(it)
        }

    }.root
}
