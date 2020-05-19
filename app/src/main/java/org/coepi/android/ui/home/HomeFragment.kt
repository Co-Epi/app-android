package org.coepi.android.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import org.coepi.android.databinding.FragmentHomeBinding.inflate
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {
    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        privacyLink.setOnClickListener {
            val webpage: Uri = Uri.parse("https://www.coepi.org/privacy/")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }

        homeCardsRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        val adapter = HomeAdapter(onItemClicked = { item ->
            viewModel.onClicked(item)
        })
        homeCardsRecyclerView.adapter = adapter
        adapter.submitList(viewModel.homeCardItems.toMutableList())

    }.root


}
