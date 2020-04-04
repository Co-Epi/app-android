package org.coepi.android.ui.debug.logs

import android.graphics.Color.BLACK
import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.graphics.Color.YELLOW
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemLogEntryBinding
import org.coepi.android.system.log.LogLevel
import org.coepi.android.system.log.LogLevel.D
import org.coepi.android.system.log.LogLevel.E
import org.coepi.android.system.log.LogLevel.I
import org.coepi.android.system.log.LogLevel.V
import org.coepi.android.system.log.LogLevel.W
import org.coepi.android.system.log.LogMessage

class LogsRecyclerViewAdapter : RecyclerView.Adapter<LogsRecyclerViewAdapter.ViewHolder>() {
    private var items = emptyList<LogMessage>()

    class ViewHolder(
        parent: ViewGroup,
        private val binding : ItemLogEntryBinding =
            ItemLogEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LogMessage): Unit = binding.run {
            this.item = item
            this.lblLogLevel.setTextColor(item.level.color())
            this.lblMessage.setTextColor(item.level.color())
        }

        private fun LogLevel.color(): Int = when (this) {
            V -> BLACK
            D -> GREEN
            I -> BLUE
            W -> YELLOW
            E -> RED
        }
    }

    fun setItems(items: List<LogMessage>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
