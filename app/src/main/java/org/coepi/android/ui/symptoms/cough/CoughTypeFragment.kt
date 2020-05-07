package org.coepi.android.ui.symptoms.cough

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.coepi.android.databinding.FragmentCoughTypeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CoughTypeFragment : Fragment() {

    private val viewModel by viewModel<CoughTypeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = FragmentCoughTypeBinding.inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        toolbar.setNavigationOnClickListener { viewModel.onBack() }
    }.root
}