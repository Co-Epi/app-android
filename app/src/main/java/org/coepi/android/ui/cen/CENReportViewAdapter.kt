package org.coepi.android.ui.cen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.R.layout.item_cen_report
import org.coepi.android.cen.RealmCenReport
import org.coepi.android.ui.cen.CENReportViewAdapter.ViewHolder

class CENReportViewAdapter : RecyclerView.Adapter<ViewHolder>() {
    private var items = emptyList<RealmCenReport>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view as TextView
        fun bind(item: RealmCenReport) {
            textView.text = item.report
        }
    }

    fun setItems(items: List<RealmCenReport>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                item_cen_report, parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
