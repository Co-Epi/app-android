package org.coepi.android.ui.symptoms.fever

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.coepi.android.databinding.FragmentFeverDurationBinding.inflate
import org.coepi.android.ui.common.KeyboardHider
import org.coepi.android.ui.extensions.onBack
import org.coepi.android.ui.extensions.onTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeverDurationFragment : Fragment() {

    private val viewModel by viewModel<FeverDurationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        feverDuration.onTextChanged {
            viewModel.onDurationChanged(it)
        }

        onBack { viewModel.onBack() }
        toolbar.setNavigationOnClickListener { viewModel.onBackPressed() }
    }.root

    override fun onStop() {
        KeyboardHider().hideKeyboard(this.requireContext(), this.requireView())
        super.onStop()
    }
}