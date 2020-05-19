package org.coepi.android.ui.symptoms.fever

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import org.coepi.android.databinding.FragmentFeverTemperatureSpotBinding.inflate
import org.coepi.android.ui.extensions.onBack
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeverTemperatureSpotFragment : Fragment() {

    private val viewModel by viewModel<FeverTemperatureSpotViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

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