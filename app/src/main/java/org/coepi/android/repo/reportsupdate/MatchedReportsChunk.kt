package org.coepi.android.repo.reportsupdate

import org.tcncoalition.tcnclient.crypto.SignedReport

data class MatchedReportsChunk(val reports: List<SignedReport>, val matched: List<SignedReport>,
                               val interval: ReportsInterval
)
