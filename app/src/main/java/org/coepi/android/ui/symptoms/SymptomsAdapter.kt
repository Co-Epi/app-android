package org.coepi.android.ui.symptoms

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemSymptomBinding
import org.coepi.android.databinding.ItemSymptomBinding.inflate
import org.coepi.android.system.log.log
import org.coepi.android.ui.symptoms.SymptomsAdapter.ViewHolder

class SymptomsAdapter(
    private val onItemChecked: (SymptomViewData) -> Unit
) : ListAdapter<SymptomViewData, ViewHolder>(SymptomDiffCallback()) {

    class ViewHolder(private val parent: ViewGroup, private val binding: ItemSymptomBinding =
        inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SymptomViewData, onChecked: (SymptomViewData) -> Unit): Unit = binding.run {
            this.item = item
            checkbox.isChecked = item.isChecked
            root.setOnClickListener {
                onChecked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemChecked)
    }
}

private class SymptomDiffCallback : ItemCallback<SymptomViewData>() {
    override fun areItemsTheSame(oldItem: SymptomViewData, newItem: SymptomViewData): Boolean =
        oldItem.symptom.id == newItem.symptom.id

    override fun areContentsTheSame(oldItem: SymptomViewData, newItem: SymptomViewData): Boolean =
        oldItem == newItem
}
