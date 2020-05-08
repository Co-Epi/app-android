package org.coepi.android.components.noop

import org.coepi.android.domain.TcnMatcher
import org.coepi.android.tcn.Tcn
import org.tcncoalition.tcnclient.crypto.SignedReport

class NoOpTcnMatcher : TcnMatcher {
    override fun match(tcns: List<Tcn>, reports: List<SignedReport>): List<SignedReport> =
        emptyList()
}
