package org.coepi.android.ui.debug.logs

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants.LONG_PRESS
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemLogEntryBinding
import org.coepi.android.databinding.ItemLogEntryBinding.inflate
import org.coepi.android.ui.debug.logs.LogsRecyclerViewAdapter.ViewHolder

class LogsRecyclerViewAdapter(private val onItemLongClick: () -> Unit)
    : ListAdapter<LogMessageViewData, ViewHolder>(LogsDiffCallback()) {

    class ViewHolder(
        parent: ViewGroup,
        private val binding : ItemLogEntryBinding =
            inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LogMessageViewData, onItemClick: () -> Unit): Unit = binding.run {
            this.item = item
            time.setTextColor(item.textColor)
            message.setTextColor(item.textColor)

            root.setOnLongClickListener {
                root.performHapticFeedback(LONG_PRESS)
                onItemClick()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemLongClick)
    }
}

private class LogsDiffCallback : ItemCallback<LogMessageViewData>() {
    override fun areItemsTheSame(oldItem: LogMessageViewData, newItem: LogMessageViewData)
            : Boolean =
        oldItem === newItem

    @SuppressLint("DiffUtilEquals")
    // 2 messages with the same content should be handled as different, so identity based
    override fun areContentsTheSame(oldItem: LogMessageViewData, newItem: LogMessageViewData)
            : Boolean =
        oldItem === newItem
}
