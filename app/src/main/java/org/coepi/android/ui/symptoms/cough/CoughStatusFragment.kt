package org.coepi.android.ui.symptoms.cough

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import org.coepi.android.databinding.FragmentCoughStatusBinding.inflate
import org.coepi.android.extensions.observeWith
import org.coepi.android.ui.extensions.onBack
import org.koin.androidx.viewmodel.ext.android.viewModel

class CoughStatusFragment  : Fragment() {

    private val viewModel by viewModel<CoughStatusViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        onBack { viewModel.onBack() }
        toolbar.setNavigationOnClickListener { viewModel.onBackPressed() }

        statusRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
        }

        val adapter = CoughStatusAdapter(onItemChecked = { item ->
            viewModel.onSelected(item)
        })

        statusRecyclerView.adapter = adapter

        viewModel.statuses.observeWith(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }
    }.root
}