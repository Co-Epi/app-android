package org.coepi.android.ui.common

sealed class UINotificationData(open val message: String) {
    data class Success(override val message: String): UINotificationData(message)
    data class Failure(override val message: String): UINotificationData(message)
}
