package org.coepi.android.ui.symptoms.earliestsymptom


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.coepi.android.databinding.FragmentEarliestSymptomBinding.inflate
import org.coepi.android.ui.extensions.onBack
import org.coepi.android.ui.extensions.onTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel

class EarliestSymptomFragment : Fragment() {

    private val viewModel by viewModel<EarliestSymptomViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                        savedInstanceState: Bundle?)
    : View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        earliestSymptom.onTextChanged {
            viewModel.onDurationChanged(it)
        }


        onBack { viewModel.onBack() }
        toolbar.setNavigationOnClickListener { viewModel.onBackPressed() }
    }.root

}