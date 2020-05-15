package org.coepi.android.ui.symptoms.fever

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import org.coepi.android.databinding.FragmentFeverTakenTodayBinding.inflate
import org.coepi.android.ui.extensions.onBack
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeverTakenTodayFragment : Fragment() {

    private val viewModel by viewModel<FeverTakenTodayViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        onBack { viewModel.onBack() }
        toolbar.setNavigationOnClickListener { viewModel.onBackPressed() }
    }.root

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.isInProgress.observe(viewLifecycleOwner, onChanged = {
            if(it) {
                this.view?.isClickable = false
            }
        })

    }
}