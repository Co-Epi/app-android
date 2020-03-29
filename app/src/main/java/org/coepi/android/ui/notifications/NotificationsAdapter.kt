package org.coepi.android.ui.notifications

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
import org.coepi.android.ui.notifications.NotificationViewAdapter.ViewHolder

class NotificationViewAdapter : RecyclerView.Adapter<ViewHolder>() {
    private var notifications = emptyList<NotificationsViewData>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view as TextView
        fun bind(item: NotificationsViewData) {
            textView.text = SpannableString(item.name).apply {
                setSpan(
                    ForegroundColorSpan(BLUE),
                    0,
                    "SEVERE".length,
                    SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }
    }

    fun setItems(items: List<NotificationsViewData>) {
        this.notifications = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(item_log_entry, parent,
            false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount() = notifications.size
}
