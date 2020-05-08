package org.coepi.android.tcn

import org.coepi.android.domain.UnixTime

data class TcnKey(
    val key: String, // Hex
    val timestamp: UnixTime
)
