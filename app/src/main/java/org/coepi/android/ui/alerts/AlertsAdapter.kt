package org.coepi.android.ui.alerts

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemAlertBinding
import org.coepi.android.databinding.ItemAlertBinding.inflate
import org.coepi.android.ui.alerts.AlertsAdapter.ViewHolder

class AlertsAdapter(
    private val onAlertClick: (AlertViewData) -> Unit
) : ListAdapter<AlertViewData, ViewHolder>(AlertsDiffCallback()) {

    class ViewHolder(
        parent: ViewGroup, private val binding: ItemAlertBinding =
            inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: AlertViewData,
            monthHeaderVisible: Boolean,
            onAlertClick: (AlertViewData) -> Unit
        ): Unit = binding.run {
            item.showMonthHeader = monthHeaderVisible
            this.item = item
            root.setOnClickListener {
                onAlertClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = getItem(position)
        var previousItem: AlertViewData? = null
        var monthHeaderVisible = true

        if (position > 0) {
            previousItem = getItem(position - 1)
        }

        previousItem?.let {
            monthHeaderVisible = currentItem.contactTimeMonth != previousItem.contactTimeMonth
        }

        holder.bind(currentItem, monthHeaderVisible, onAlertClick)
    }
}

private class AlertsDiffCallback : ItemCallback<AlertViewData>() {
    override fun areItemsTheSame(oldItem: AlertViewData, newItem: AlertViewData): Boolean =
        oldItem.report.id == newItem.report.id

    override fun areContentsTheSame(oldItem: AlertViewData, newItem: AlertViewData): Boolean =
        oldItem == newItem
}
