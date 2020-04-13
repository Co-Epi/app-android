package org.coepi.android.ui.debug.ble

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.coepi.android.databinding.ItemDebugBleHeaderBinding
import org.coepi.android.databinding.ItemDebugBleItemBinding
import org.coepi.android.ui.debug.ble.DebugBleItemViewData.Header
import org.coepi.android.ui.debug.ble.DebugBleItemViewData.Item

class DebugBleRecyclerAdapter
    : ListAdapter<DebugBleItemViewData, ViewHolder>(SymptomDiffCallback()) {

    class HeaderViewHolder(
        private val parent: ViewGroup,
        private val binding: ItemDebugBleHeaderBinding =
            ItemDebugBleHeaderBinding.inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Header): Unit = binding.run {
            this.item = item
        }
    }

    class ItemViewHolder(
        private val parent: ViewGroup,
        private val binding: ItemDebugBleItemBinding =
            ItemDebugBleItemBinding.inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item): Unit = binding.run {
            this.item = item
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Header -> 0
        is Item -> 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            0 -> HeaderViewHolder(parent)
            1 -> ItemViewHolder(parent)
            else -> error("Not supported: $viewType")
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Header -> (holder as HeaderViewHolder).bind(item)
            is Item -> (holder as ItemViewHolder).bind(item)
        }
    }
}

private class SymptomDiffCallback : ItemCallback<DebugBleItemViewData>() {
    override fun areItemsTheSame(oldItem: DebugBleItemViewData, newItem: DebugBleItemViewData): Boolean =
        oldItem.text == newItem.text

    override fun areContentsTheSame(oldItem: DebugBleItemViewData, newItem: DebugBleItemViewData): Boolean =
        oldItem == newItem
}
