package org.coepi.android.ui.symptoms.breathless

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemBreathlessBinding
import org.coepi.android.databinding.ItemBreathlessBinding.inflate
import org.coepi.android.ui.symptoms.breathless.BreathlessAdapter.ViewHolder

class BreathlessAdapter(
    private val onItemChecked: (BreathlessViewData) -> Unit
) : ListAdapter<BreathlessViewData, ViewHolder>(BreathlessDiffCallback()) {

    class ViewHolder(private val parent: ViewGroup, private val binding: ItemBreathlessBinding =
        inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BreathlessViewData, onChecked: (BreathlessViewData) -> Unit): Unit = binding.run {
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

private class BreathlessDiffCallback : ItemCallback<BreathlessViewData>() {
    override fun areItemsTheSame(oldItem: BreathlessViewData, newItem: BreathlessViewData): Boolean =
        oldItem.breathless == newItem.breathless

    override fun areContentsTheSame(oldItem: BreathlessViewData, newItem: BreathlessViewData): Boolean =
        oldItem == newItem
}