package org.coepi.android.ui.symptoms.cough


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import org.coepi.android.databinding.FragmentCoughDurationBinding.inflate
import org.coepi.android.ui.extensions.onBack
import org.coepi.android.ui.extensions.onTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel

class CoughDurationFragment : Fragment() {

    private val viewModel by viewModel<CoughDurationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        coughDuration.onTextChanged {
            viewModel.onDurationChanged(it)
        }

        onBack { viewModel.onBack() }
        toolbar.setNavigationOnClickListener { viewModel.onBackPressed() }
    }.root

    override fun onStop() {
        val imm: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        super.onStop()
    }
}