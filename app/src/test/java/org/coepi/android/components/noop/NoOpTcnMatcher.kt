package org.coepi.android.components.noop

import org.coepi.android.domain.TcnMatcher
import org.coepi.android.repo.reportsupdate.MatchedReport
import org.coepi.android.tcn.ReceivedTcn
import org.coepi.android.tcn.Tcn
import org.tcncoalition.tcnclient.crypto.SignedReport

class NoOpTcnMatcher : TcnMatcher {
    override fun match(tcns: List<ReceivedTcn>, reports: List<SignedReport>): List<MatchedReport> =
        emptyList()
}
