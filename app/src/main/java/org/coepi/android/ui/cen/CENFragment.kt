package org.coepi.android.ui.cen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.Fragment
import org.coepi.android.databinding.FragmentBleBinding.inflate
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import kotlinx.android.synthetic.main.fragment_ble.*
import org.coepi.android.cen.Cen
import org.coepi.android.cen.RealmCenDao
import org.coepi.android.extensions.observeWith
import org.coepi.android.repo.RealmProvider

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

        var curcen = viewModel.curcen.toString()

        textCENReport.setText(curcen.toCharArray(),0,curcen.length);
        postReport.setOnClickListener(){
            val cen_from_other = textCENReport.text;
            var cenDao: RealmCenDao = RealmCenDao(RealmProvider(view.context));
            cenDao.insert(Cen(cen_from_other.toString(), (System.currentTimeMillis() / 1000L).toInt()))
        }
    }
}
