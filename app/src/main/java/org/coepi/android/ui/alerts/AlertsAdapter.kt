package org.coepi.android.ui.alerts

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemAlertBinding
import org.coepi.android.databinding.ItemAlertBinding.inflate
import org.coepi.android.databinding.ItemAlertHeaderBinding
import org.coepi.android.system.log.log
import org.coepi.core.domain.model.Alert
import org.coepi.android.ui.alerts.AlertCellViewData.Header
import org.coepi.android.ui.alerts.AlertCellViewData.Item

class AlertsAdapter(
    private val onAlertClick: (AlertViewData) -> Unit,
    private val onAlertDismissed: (Alert) -> Unit
) : ListAdapter<AlertCellViewData, RecyclerView.ViewHolder>(AlertsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> AlertHeaderViewHolder(parent)
            1 -> AlertItemViewHolder(parent)
            else -> error("Not handled: $viewType")
        }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Header -> 0
        is Item -> 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Header -> (holder as AlertHeaderViewHolder).bind(item)
            is Item -> (holder as AlertItemViewHolder).bind(item, onAlertClick)
        }
    }

    fun removeAt(position: Int) {
        when (val item = getItem(position)) {
            is Header -> log.e("Trying to remove directly a header. Ignoring.")
            is Item -> onAlertDismissed(item.viewData.alert)
        }
    }

    class AlertHeaderViewHolder(
        private val parent: ViewGroup,
        private val binding: ItemAlertHeaderBinding =
            ItemAlertHeaderBinding.inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewData: Header): Unit = binding.run {
            this.viewData = viewData
        }
    }

    class AlertItemViewHolder(
        parent: ViewGroup, private val binding: ItemAlertBinding =
            inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, onAlertClick: (AlertViewData) -> Unit): Unit = binding.run {
            this.viewData = item.viewData
            root.setOnClickListener {
                onAlertClick(item.viewData)
            }
        }
    }
}

private class AlertsDiffCallback : ItemCallback<AlertCellViewData>() {
    override fun areItemsTheSame(oldItem: AlertCellViewData, newItem: AlertCellViewData): Boolean =
        when {
            oldItem is Header && newItem is Header -> oldItem.text == newItem.text
            oldItem is Item && newItem is Item -> oldItem.viewData.alert.id == newItem.viewData.alert.id
            else -> false
        }

    override fun areContentsTheSame(
        oldItem: AlertCellViewData,
        newItem: AlertCellViewData
    ): Boolean = oldItem == newItem
}
