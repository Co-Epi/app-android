package org.coepi.android.cen

import java.util.Date

data class CenReport(
    val id: String,
    val report: String,
    val keys: String,
    val reportMimeType: String,
    val date: Date,
    val isUser: Boolean
)
