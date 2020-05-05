package org.coepi.android.repo.reportsupdate

import org.tcncoalition.tcnclient.crypto.SignedReport

data class SignedReportsChunk(val interval: ReportsInterval, val reports: List<SignedReport>)
