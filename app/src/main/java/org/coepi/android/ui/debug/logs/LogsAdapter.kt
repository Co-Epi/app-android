package org.coepi.android.ui.debug.logs

import android.graphics.Color.BLACK
import android.graphics.Color.BLUE
import android.graphics.Color.GREEN
import android.graphics.Color.RED
import android.graphics.Color.YELLOW
import android.text.Spannable.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.R.layout.item_log_entry
import org.coepi.android.system.log.LogLevel
import org.coepi.android.system.log.LogLevel.D
import org.coepi.android.system.log.LogLevel.E
import org.coepi.android.system.log.LogLevel.I
import org.coepi.android.system.log.LogLevel.V
import org.coepi.android.system.log.LogLevel.W
import org.coepi.android.system.log.LogMessage

class LogsRecyclerViewAdapter : RecyclerView.Adapter<LogsRecyclerViewAdapter.ViewHolder>() {
    private var items = emptyList<LogMessage>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view as TextView
        fun bind(item: LogMessage) {
            textView.text = SpannableString(item.toText()).apply {
                setSpan(
                    ForegroundColorSpan(item.level.color()),
                    0,
                    item.level.toText().length,
                    SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }

        private fun LogMessage.toText() = "${level.toText()} $text"

        private fun LogLevel.color(): Int = when (this) {
            V -> BLACK
            D -> GREEN
            I -> BLUE
            W -> YELLOW
            E -> RED
        }

        private fun LogLevel.toText() = when (this) {
            V -> "VERBOSE"
            D -> "DEBUG"
            I -> "INFO"
            W -> "WARNING"
            E -> "ERROR"
        }
    }

    fun setItems(items: List<LogMessage>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(item_log_entry, parent,
            false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
