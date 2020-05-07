package org.coepi.android.ui.symptoms.cough

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.coepi.android.databinding.FragmentCoughStatusBinding
import org.coepi.android.extensions.observeWith
import org.koin.androidx.viewmodel.ext.android.viewModel

class CoughStatusFragment  : Fragment() {

    private val viewModel by viewModel<CoughStatusViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = FragmentCoughStatusBinding.inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        toolbar.setNavigationOnClickListener { viewModel.onBack() }

        productsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        val adapter = CoughStatusAdapter(onItemChecked = { item ->
            //viewModel.onChecked(item)
        })
        productsRecyclerView.adapter = adapter

        viewModel.statuses.observeWith(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }
    }.root
}