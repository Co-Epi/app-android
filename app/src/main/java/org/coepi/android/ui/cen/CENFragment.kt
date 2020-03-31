package org.coepi.android.ui.cen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.coepi.android.databinding.FragmentBleBinding.inflate
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import kotlinx.android.synthetic.main.fragment_ble.*
import org.coepi.android.extensions.observeWith

class CENFragment : Fragment() {
    private val viewModel by viewModel<CENViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {

        // set up contacts recycler view
        val neighborCENAdapter = CENRecyclerViewAdapter()
        neighborCENsRecyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = neighborCENAdapter
        }

        viewModel.neighborCENs.observeWith(viewLifecycleOwner) {
            neighborCENAdapter.setItems(it)
        }

        viewModel.myCurrentCEN.observeWith(viewLifecycleOwner) {
            android.util.Log.i("BleFragment", "BleFragment new value: $it")
            textMyCurrentCEN.text = it
        }
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //init this to curCen so that so that we can copy and send to other mobile
        val curCEN = viewModel.curcen.toString()
        textCENReport.setText(curCEN.toCharArray(), 0, curCEN.length);

        //user has pasted a CEN, simulate BLE reception by inserting it
        postReport.setOnClickListener() {
            val cenFromOther = textCENReport.text;
            viewModel.insertPastedCEN(cenFromOther.toString());
        }
    }
}
