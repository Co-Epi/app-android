package org.coepi.android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.databinding.FragmentUserSettingsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.coepi.android.system.WebpageShower
import org.coepi.android.system.log.log
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserSettingsFragment : Fragment() {
    private val viewModel by viewModel<UserSettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        val adapter = UserSettingsAdapter(
            onToggle = { item, toggled ->
                viewModel.onToggle(item, toggled)
            },
            onClick = { item ->
                activity?.let { activity ->
                    viewModel.onClick(item, activity)
                } ?: {
                    log.w("No activity set clicking on setting")
                }()
            }
        )

        viewModel.showWeb.observeWith(viewLifecycleOwner) { uri ->
            activity?.let {
                WebpageShower().show(it, uri)
            }
        }

        viewModel.settings.observeWith(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        recyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            this.adapter = adapter
        }
    }.root
}
