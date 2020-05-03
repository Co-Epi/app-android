package org.coepi.android.repo.reportsupdate

import org.tcncoalition.tcnclient.crypto.SignedReport

data class ProcessedReportsChunk(val reports: List<String>, val matched: List<SignedReport>,
                                 val interval: ReportsInterval
)
