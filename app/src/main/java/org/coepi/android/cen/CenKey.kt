package org.coepi.android.cen

import org.coepi.android.domain.UnixTime

data class CenKey(
    val key: String, // Hex
    val timestamp: UnixTime
)
