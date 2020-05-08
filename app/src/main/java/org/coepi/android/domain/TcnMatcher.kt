package org.coepi.android.domain

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.coepi.android.extensions.toHex
import org.coepi.android.tcn.Tcn
import org.tcncoalition.tcnclient.crypto.SignedReport

interface TcnMatcher {
    fun match(tcns: List<Tcn>, reports: List<SignedReport>): List<SignedReport>
}

class TcnMatcherImpl : TcnMatcher {

    override fun match(tcns: List<Tcn>, reports: List<SignedReport>): List<SignedReport> =
        if (tcns.isEmpty()) {
            emptyList()
        } else {
            runBlocking {
                matchSuspended(tcns, reports)
            }
        }

    private suspend fun matchSuspended(tcns: List<Tcn>, reports: List<SignedReport>)
            : List<SignedReport> =
        coroutineScope {
            val tcnsSet: Set<String> = tcns.map { it.toHex() }.toHashSet()
            reports.distinct().map { report ->
                async(Default) {
                    match(tcnsSet, report)
                }
            }.awaitAll().filterNotNull()
        }

    fun match(tcnsSet: Set<String>, signedReport: SignedReport): SignedReport? {
        val tcns = signedReport.report.temporaryContactNumbers
        return if (tcns.asSequence().any { tcnsSet.contains(it.bytes.toHex()) }) {
            signedReport
        } else {
            null
        }
    }
}
