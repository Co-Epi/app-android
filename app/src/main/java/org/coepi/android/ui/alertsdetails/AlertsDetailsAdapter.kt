package org.coepi.android.ui.alertsdetails

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemAlertDetailsBinding
import org.coepi.android.databinding.ItemAlertDetailsBinding.inflate
import org.coepi.android.ui.alertsdetails.AlertsDetailsAdapter.ItemViewHolder

class AlertsDetailsAdapter : ListAdapter<AlertDetailsSymptomViewData, ItemViewHolder>(
    AlertsDetailsDiffCallback()
) {

    class ItemViewHolder(
        parent: ViewGroup, private val binding: ItemAlertDetailsBinding =
            inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AlertDetailsSymptomViewData): Unit = binding.run {
            this.item = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(parent)

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class AlertsDetailsDiffCallback : ItemCallback<AlertDetailsSymptomViewData>() {
    override fun areItemsTheSame(
        oldItem: AlertDetailsSymptomViewData,
        newItem: AlertDetailsSymptomViewData
    ): Boolean =
        oldItem.symptom == newItem.symptom

    override fun areContentsTheSame(
        oldItem: AlertDetailsSymptomViewData,
        newItem: AlertDetailsSymptomViewData
    ): Boolean =
        oldItem == newItem
}
