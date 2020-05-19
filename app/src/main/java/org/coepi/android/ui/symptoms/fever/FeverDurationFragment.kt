package org.coepi.android.ui.symptoms.fever


import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import org.coepi.android.databinding.FragmentFeverDurationBinding
import org.coepi.android.databinding.FragmentFeverDurationBinding.inflate
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
        hideKeyboardFrom(this.requireContext(),
            this.requireView())
        super.onStop()
    }

    private fun hideKeyboardFrom(
        context: Context,
        view: View
    ) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}