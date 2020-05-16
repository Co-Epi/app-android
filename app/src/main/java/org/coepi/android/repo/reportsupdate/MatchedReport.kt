package org.coepi.android.repo.reportsupdate

import org.coepi.android.domain.UnixTime
import org.tcncoalition.tcnclient.crypto.SignedReport

data class MatchedReport(val report: SignedReport, val contactTime: UnixTime)
