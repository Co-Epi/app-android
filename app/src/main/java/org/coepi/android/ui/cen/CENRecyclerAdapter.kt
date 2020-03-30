package org.coepi.android.ui.cen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.R.layout.item_cen
import org.coepi.android.cen.RealmCen
import org.coepi.android.ui.cen.CENRecyclerViewAdapter.ViewHolder

class CENRecyclerViewAdapter : RecyclerView.Adapter<ViewHolder>() {
    private var items = emptyList<String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView = view as TextView
        fun bind(item: String) {
            textView.text = item
        }

    }

    fun setItems(items: List<String>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                item_cen, parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
