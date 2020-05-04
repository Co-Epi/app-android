package org.coepi.android.ui.symptoms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import org.coepi.android.databinding.FragmentSymptomsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.koin.androidx.viewmodel.ext.android.viewModel

class SymptomsFragment : Fragment() {
    private val viewModel by viewModel<SymptomsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        toolbar.setNavigationOnClickListener { viewModel.onBack() }

        productsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
        }

        val adapter = SymptomsAdapter(onItemChecked = { item ->
            viewModel.onChecked(item)
        })
        productsRecyclerView.adapter = adapter

        viewModel.symptoms.observeWith(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }
    }.root
}
