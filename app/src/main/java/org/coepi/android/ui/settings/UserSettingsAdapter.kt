package org.coepi.android.ui.settings

import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.coepi.android.databinding.ItemSettingSectionHeaderBinding
import org.coepi.android.databinding.ItemSettingTextBinding
import org.coepi.android.databinding.ItemSettingToggleBinding
import org.coepi.android.ui.settings.UserSettingViewData.SectionHeader
import org.coepi.android.ui.settings.UserSettingViewData.Text
import org.coepi.android.ui.settings.UserSettingViewData.Toggle

class UserSettingsAdapter(
    private val onToggle: (Toggle, Boolean) -> Unit,
    private val onClick: (Text) -> Unit
) : ListAdapter<UserSettingViewData, RecyclerView.ViewHolder>(SettingsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> SectionHeaderViewHolder(parent)
            1 -> ToggleViewHolder(parent)
            2 -> TextViewHolder(parent)
            else -> error("Not handled: $viewType")
        }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is SectionHeader -> 0
        is Toggle -> 1
        is Text -> 2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SectionHeader -> (holder as SectionHeaderViewHolder).bind(item)
            is Toggle -> (holder as ToggleViewHolder).bind(item, onToggle)
            is Text -> (holder as TextViewHolder).bind(item, onClick)
        }
    }

    class SectionHeaderViewHolder(
        private val parent: ViewGroup,
        private val binding: ItemSettingSectionHeaderBinding =
            ItemSettingSectionHeaderBinding.inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewData: SectionHeader): Unit = binding.run {
            this.viewData = viewData
        }
    }

    class ToggleViewHolder(
        private val parent: ViewGroup,
        private val binding: ItemSettingToggleBinding =
            ItemSettingToggleBinding.inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewData: Toggle, onToggle: (Toggle, Boolean) -> Unit): Unit = binding.run {
            this.viewData = viewData
            toggle.setOnCheckedChangeListener { _, isChecked ->
                onToggle(viewData, isChecked)
            }
        }
    }

    class TextViewHolder(
        parent: ViewGroup, private val binding: ItemSettingTextBinding =
            ItemSettingTextBinding.inflate(from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Text, onClick: (Text) -> Unit): Unit = binding.run {
            this.viewData = item
            root.setOnClickListener {
                onClick(item)
            }
        }
    }
}

private class SettingsDiffCallback : ItemCallback<UserSettingViewData>() {
    override fun areItemsTheSame(oldItem: UserSettingViewData, newItem: UserSettingViewData): Boolean =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: UserSettingViewData, newItem: UserSettingViewData): Boolean =
        oldItem == newItem
}
