package org.coepi.android.domain

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.coepi.android.tcn.Tcn
import org.coepi.android.extensions.base64ToByteArray
import org.coepi.android.extensions.toHex
import org.tcncoalition.tcnclient.crypto.SignedReport

interface TcnMatcher {
    fun match(tcns: List<Tcn>, reports: List<String>): List<SignedReport>
}

class TcnMatcherImpl : TcnMatcher {

    override fun match(tcns: List<Tcn>, reports: List<String>): List<SignedReport> =
        if (tcns.isEmpty()) {
            emptyList()
        } else {
            runBlocking {
                matchSuspended(tcns, reports)
            }
        }

    // TODO dependency
    private fun toReport(apiReport: String): SignedReport? =
        apiReport.base64ToByteArray()?.let { SignedReport.fromByteArray(it) }

    private suspend fun matchSuspended(tcns: List<Tcn>, reports: List<String>): List<SignedReport> =
        coroutineScope {
            val tcnsSet: Set<String> = tcns.map { it.toHex() }.toHashSet()
            reports.distinct().map { report ->
                async(Default) {
                    match(tcnsSet, report)
                }
            }.awaitAll().filterNotNull()
        }

    private fun match(tcnsSet: Set<String>, reportString: String): SignedReport? {
        val signedReport: SignedReport = toReport(reportString) ?: return null
        val tcns = signedReport.report.temporaryContactNumbers
        return if (tcns.asSequence().any { tcnsSet.contains(it.bytes.toHex()) }) {
            signedReport
        } else {
            null
        }
    }
}
