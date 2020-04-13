package org.coepi.android.cen

import org.coepi.android.domain.CoEpiDate

data class SymptomReport(
    var id: String,
    var report: String,
    var date: CoEpiDate
)
