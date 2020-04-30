package org.coepi.android.domain

import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.coepi.android.cen.Cen
import org.coepi.android.extensions.base64ToByteArray
import org.coepi.android.extensions.toHex
import org.tcncoalition.tcnclient.crypto.SignedReport

interface CenMatcher {
    fun match(tcns: List<Cen>, reports: List<String>): List<SignedReport>
}

class CenMatcherImpl : CenMatcher {

    override fun match(tcns: List<Cen>, reports: List<String>): List<SignedReport> =
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

    private suspend fun matchSuspended(cens: List<Cen>, reports: List<String>): List<SignedReport> =
        coroutineScope {
            val censSet: Set<String> = cens.map { it.toHex() }.toHashSet()
            reports.distinct().map { report ->
                async(Default) {
                    match(censSet, report)
                }
            }.awaitAll().filterNotNull()
        }

    private fun match(censSet: Set<String>, reportString: String): SignedReport? {
        val signedReport: SignedReport = toReport(reportString) ?: return null

        val recomputedTemporaryContactNumbers = signedReport.report.temporaryContactNumbers
        recomputedTemporaryContactNumbers.forEach {
            if (censSet.contains(it.bytes.toHex())) {
                return signedReport
            }
        }

        return null
    }
}
