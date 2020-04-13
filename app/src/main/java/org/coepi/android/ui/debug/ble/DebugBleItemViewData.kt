package org.coepi.android.ui.debug.ble

sealed class DebugBleItemViewData(open val text: String) {
    data class Header(override val text: String): DebugBleItemViewData(text)
    data class Item(override val text: String): DebugBleItemViewData(text)
}
