package org.coepi.android.domain

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.coepi.android.extensions.toHex
import org.coepi.android.repo.reportsupdate.MatchedReport
import org.coepi.android.tcn.ReceivedTcn
import org.tcncoalition.tcnclient.crypto.SignedReport

interface TcnMatcher {
    fun match(tcns: List<ReceivedTcn>, reports: List<SignedReport>): List<MatchedReport>
}

class TcnMatcherImpl : TcnMatcher {

    override fun match(tcns: List<ReceivedTcn>, reports: List<SignedReport>): List<MatchedReport> =
        if (tcns.isEmpty()) {
            emptyList()
        } else {
            runBlocking {
                matchSuspended(tcns, reports)
            }
        }

    private suspend fun matchSuspended(tcns: List<ReceivedTcn>, reports: List<SignedReport>)
            : List<MatchedReport> =
        coroutineScope {
            // Put TCNs in a map for quicker lookup
            // NOTE: If there are repeated TCNs, only the last is used.
            val tcnsMap: Map<String, ReceivedTcn> = tcns.associateBy { it.tcn.toHex() }
            reports.distinct().map { report ->
                async(Default) {
                    match(tcnsMap, report)
                }
            }.awaitAll().filterNotNull()
        }

    private fun match(tcns: Map<String, ReceivedTcn>, signedReport: SignedReport): MatchedReport? {
        val reportTcns = signedReport.report.temporaryContactNumbers
            .asSequence()

        for (reportTcn in reportTcns) {
            val matchingTcn: ReceivedTcn? = tcns[reportTcn.bytes.toHex()]
            if (matchingTcn != null) {
                return MatchedReport(signedReport, matchingTcn.timestamp)
            }
        }
        return null
    }
}
