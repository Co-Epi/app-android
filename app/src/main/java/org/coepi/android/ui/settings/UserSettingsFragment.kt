package org.coepi.android.ui.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import org.coepi.android.databinding.FragmentUserSettingsBinding.inflate
import org.coepi.android.extensions.observeWith
import org.coepi.android.system.log.log
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class UserSettingsFragment : Fragment() {
    private val viewModel by viewModel<UserSettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflate(inflater, container, false).apply {
        lifecycleOwner = viewLifecycleOwner
        vm = viewModel

        val uri = getDbFileUri("databases/db.sqlite")

        val adapter = UserSettingsAdapter(
            onToggle = { item, toggled ->
                viewModel.onToggle(item, toggled)
            },
            onClick = { item ->
                activity?.let { activity ->
                    viewModel.onClick(item, activity, uri)
                } ?: {
                    log.w("No activity set clicking on setting")
                }()
            }
        )

        viewModel.settings.observeWith(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        recyclerView.run {
            layoutManager = LinearLayoutManager(inflater.context, VERTICAL, false)
            this.adapter = adapter
        }
    }.root

    fun getDbFileUri(filenameSuffix: String): Uri? {

        var file: File? = File(
            context?.getFilesDir()?.getParent(),
            filenameSuffix
        )
        var dbUri: Uri? = context?.let {
            file?.let { it1 ->
                val uriForFile = FileProvider.getUriForFile(
                    it,//this@MainActivity,
                    "org.coepi.provider",  //(use your app signature + ".provider" )
                    it1
                )
                uriForFile
            }
        }

        return dbUri
    }

}
