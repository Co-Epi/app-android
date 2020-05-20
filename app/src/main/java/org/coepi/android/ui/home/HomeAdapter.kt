package org.coepi.android.ui.home

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemHomeCardBinding
import org.coepi.android.databinding.ItemHomeCardBinding.inflate
import org.coepi.android.ui.home.HomeAdapter.ViewHolder

class HomeAdapter(
    private val onItemClicked: (HomeCard) -> Unit
) : ListAdapter<HomeCard, ViewHolder>(HomeItemDiffCallback()) {

    class ViewHolder(
        private val parent: ViewGroup, private val binding: ItemHomeCardBinding =
            inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HomeCard, onClick: (HomeCard) -> Unit): Unit = binding.run {
            this.item = item
            root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
    }
}

private class HomeItemDiffCallback : ItemCallback<HomeCard>() {
    override fun areItemsTheSame(oldItem: HomeCard, newItem: HomeCard): Boolean =
        oldItem.cardId == newItem.cardId

    override fun areContentsTheSame(oldItem: HomeCard, newItem: HomeCard): Boolean =
        oldItem == newItem
}
