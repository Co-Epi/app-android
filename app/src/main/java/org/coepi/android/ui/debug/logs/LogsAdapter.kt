package org.coepi.android.ui.debug.logs

import android.annotation.SuppressLint
import android.graphics.Color.BLACK
import android.graphics.Color.BLUE
import android.graphics.Color.RED
import android.graphics.Color.YELLOW
import android.graphics.Color.parseColor
import android.view.HapticFeedbackConstants.LONG_PRESS
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemLogEntryBinding
import org.coepi.android.databinding.ItemLogEntryBinding.inflate
import org.coepi.android.system.log.LogLevel
import org.coepi.android.system.log.LogLevel.D
import org.coepi.android.system.log.LogLevel.E
import org.coepi.android.system.log.LogLevel.I
import org.coepi.android.system.log.LogLevel.V
import org.coepi.android.system.log.LogLevel.W
import org.coepi.android.system.log.LogMessage
import org.coepi.android.ui.debug.logs.LogsRecyclerViewAdapter.ViewHolder

class LogsRecyclerViewAdapter(private val onItemLongClick: () -> Unit)
    : ListAdapter<LogMessage, ViewHolder>(LogsDiffCallback()) {

    class ViewHolder(
        parent: ViewGroup,
        private val binding : ItemLogEntryBinding =
            inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LogMessage, onItemClick: () -> Unit): Unit = binding.run {
            this.item = item
            this.lblLogLevel.setTextColor(item.level.color())
            this.lblMessage.setTextColor(item.level.color())

            root.setOnLongClickListener {
                root.performHapticFeedback(LONG_PRESS)
                onItemClick()
                true
            }
        }

        private fun LogLevel.color(): Int = when (this) {
            V -> BLACK
            D -> parseColor("#228C22")
            I -> BLUE
            W -> YELLOW
            E -> RED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemLongClick)
    }
}

private class LogsDiffCallback : ItemCallback<LogMessage>() {
    override fun areItemsTheSame(oldItem: LogMessage, newItem: LogMessage): Boolean =
        oldItem === newItem

    @SuppressLint("DiffUtilEquals")
    // 2 messages with the same content should be handled as different, so identity based
    override fun areContentsTheSame(oldItem: LogMessage, newItem: LogMessage): Boolean =
        oldItem === newItem
}
