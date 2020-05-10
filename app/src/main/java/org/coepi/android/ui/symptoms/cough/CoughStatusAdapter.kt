package org.coepi.android.ui.symptoms.cough

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemCoughStatusBinding
import org.coepi.android.databinding.ItemCoughStatusBinding.inflate
import org.coepi.android.ui.symptoms.cough.CoughStatusAdapter.ViewHolder

class CoughStatusAdapter(
    private val onItemChecked: (CoughStatusViewData) -> Unit
) : ListAdapter<CoughStatusViewData, ViewHolder>(CoughStatusDiffCallback()) {

    class ViewHolder(private val parent: ViewGroup, private val binding: ItemCoughStatusBinding =
        inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CoughStatusViewData, onChecked: (CoughStatusViewData) -> Unit): Unit = binding.run {
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

private class CoughStatusDiffCallback : ItemCallback<CoughStatusViewData>() {
    override fun areItemsTheSame(oldItem: CoughStatusViewData, newItem: CoughStatusViewData): Boolean =
        oldItem.status == newItem.status

    override fun areContentsTheSame(oldItem: CoughStatusViewData, newItem: CoughStatusViewData): Boolean =
        oldItem == newItem
}