package org.coepi.android.ui.alertsdetails

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemAlertDetailsLinkedAlertBinding
import org.coepi.android.databinding.ItemAlertDetailsLinkedAlertBinding.inflate
import org.coepi.android.ui.alertsdetails.AlertsDetailsLinkedAlertsAdapter.ViewHolder

class AlertsDetailsLinkedAlertsAdapter :
    ListAdapter<LinkedAlertViewData, ViewHolder>(AlertsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val parent: ViewGroup,
        private val binding: ItemAlertDetailsLinkedAlertBinding =
            inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewData: LinkedAlertViewData): Unit = binding.run {
            this.viewData = viewData
        }
    }
}

private class AlertsDiffCallback : ItemCallback<LinkedAlertViewData>() {
    override fun areItemsTheSame(oldItem: LinkedAlertViewData, newItem: LinkedAlertViewData)
            : Boolean = oldItem.alert.id == newItem.alert.id

    override fun areContentsTheSame(
        oldItem: LinkedAlertViewData,
        newItem: LinkedAlertViewData
    ): Boolean = oldItem == newItem
}
