package org.coepi.android.ui.debug.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import io.realm.Realm
import org.coepi.android.R.drawable.ic_close
import org.coepi.android.databinding.FragmentLogsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.koin.androidx.viewmodel.ext.android.viewModel
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_logs.*
import org.coepi.android.modelrealm.KeyGenerated
import org.coepi.android.modelrealm.key_gen_duration
import org.coepi.android.system.log.log
import java.util.Date

class LogsFragment: Fragment() {
    private val viewModel by viewModel<LogsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {

        // Initialize Realm (just once per application)
        Realm.init(context)


        toolbar.setNavigationIcon(ic_close)
        toolbar.setNavigationOnClickListener {
            viewModel.onCloseClick()
        }

        val logsAdapter = LogsRecyclerViewAdapter()

        logsRecyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            adapter = logsAdapter
        }

        viewModel.logs.observeWith(viewLifecycleOwner) {
            logsAdapter.setItems(it)
        }

    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            button_keygen.setOnClickListener {view ->
                log.i("Getting key:");
                // Get a Realm instance for this thread
                var realm = Realm.getDefaultInstance()
                //find a key that expires after now:
                val keys = realm.where<KeyGenerated>().greaterThan("expires", Date()).findAll();
                var key: String? = null;
                var kexpires : Date? = null;
                if( keys.size > 0 ){
                    val keygen = keys[0];

                    if( keygen != null ) {
                        key = keygen.key;
                        kexpires = keygen.expires
                    }
                }
                var msg :String = "";
                if( key == null ){
                    key = Math.random().toString();
                    realm.beginTransaction();
                    val keygen = KeyGenerated();
                    keygen.key = key;
                    kexpires = Date( Date().time + key_gen_duration );
                    keygen.expires = kexpires// expires in 1 week
                    realm.copyToRealm(keygen);
                    msg ="Generated key:${key}, expires ${kexpires}";
                    realm.commitTransaction();
                }else{
                    msg = "Found key:${key}, expires ${kexpires}";
                }
                val toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
                toast.show()

                log.i(msg);
            }

        /*button_contact.setOnClickListener { _ ->

            var c = "Contact()";

        }*/

        /*button_symptom.setOnClickListener { _ ->

            val symptomsString = "Severe";
            val serviceIntent = Intent().apply{ putExtra("symptoms", symptomsString)};
            context?.startService(serviceIntent);
        }

        button_check.setOnClickListener {
            val uuIDs: List<Contact>? = model.listContacts(0, 99999999999)
            val exposureCheck = ExposureCheck(uuIDs)
            model.onExposureCheck(exposureCheck)
        }*/

    }
}
