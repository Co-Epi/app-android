package org.tcncoalition.tcnclient.crypto

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.nield.kotlinstatistics.WeightedCoin
import java.security.SecureRandom
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

class BasicFunctionality {
    @ExperimentalUnsignedTypes
    @Test
    fun generateTemporaryContactNumbersAndReportThem() {
        // Generate a report authorization key.  This key represents the capability
        // to publish a report about a collection of derived temporary contact numbers.
        val rak = ReportAuthorizationKey(SecureRandom.getInstanceStrong())

        // Use the temporary contact key ratchet mechanism to compute a list
        // of temporary contact numbers.
        var tck = rak.initialTemporaryContactKey // tck <- tck_1
        val tcns = (0..100).map {
            val tcn = tck.temporaryContactNumber
            tck = tck.ratchet()!!
            tcn
        }

        // Prepare a report about a subset of the temporary contact numbers.
        val signedReport = rak.createReport(
            MemoType.CoEpiV1,
            "symptom data".toByteArray(Charsets.UTF_8),
            20.toUShort(),
            90.toUShort()
        )

        // Verify the source integrity of the report...
        val report = signedReport.verify()

        // ...allowing the disclosed TCNs to be recomputed.
        val recomputedTcns = report.temporaryContactNumbers.asSequence().toList()

        // Check that the recomputed TCNs match the originals.
        // The slice is offset by 1 because tcn_0 is not included.
        assertEquals(tcns.slice(20 - 1 until 90 - 1), recomputedTcns)
    }

    @ExperimentalTime
    @ExperimentalUnsignedTypes
    @Test
    fun matchSet() {
        // Simulate many users generating TCNs, some of them being observed,
        // and comparison of observed TCNs against report data.

        // Parameters.
        val numReports = 10_000;
        val tcnsPerReport = 24 * 60 / 15
        val tcnObservation = WeightedCoin(0.001)

        // Store observed TCNs.
        val observedTcns = linkedSetOf<TemporaryContactNumber>()

        // Generate some TCNs that will be reported.
        val reports = (0 until numReports)
            .map {
                val rak = ReportAuthorizationKey(SecureRandom.getInstanceStrong());
                var tck = rak.initialTemporaryContactKey;
                (1 until tcnsPerReport).asSequence().map {
                    if (tcnObservation.flip()) {
                        observedTcns.add(tck.temporaryContactNumber);
                    }
                    tck = tck.ratchet()!! // tcnsPerReport < u16::MAX
                }

                rak.createReport(
                    MemoType.CoEpiV1,
                    ByteArray(0),
                    1.toUShort(),
                    tcnsPerReport.toUShort()
                )
            }

        // The current observedTcns are exactly the ones that we expect will be reported.
        val expectedReportedTcns = observedTcns.clone();

        // Generate some extra TCNs that will not be reported.
        run {
            val rak = ReportAuthorizationKey(SecureRandom.getInstanceStrong());
            var tck = rak.initialTemporaryContactKey
            for (i in 1 until 60_000) {
                observedTcns.add(tck.temporaryContactNumber);
                tck = tck.ratchet()!! // 60_000 < u16::MAX
            }
        }

        println("Expanding candidates")

        val candidateTcns = TimeSource.Monotonic.measureTimedValue {
            // Now expand the reports into a second set of candidates.
            val candidateTcns = linkedSetOf<TemporaryContactNumber>()
            for (signedReport in reports) {
                val report = signedReport.verify()
                candidateTcns.addAll(report.temporaryContactNumbers.asSequence())
            }
            candidateTcns
        }

        println("Comparing ${candidateTcns.value.size} candidates against ${observedTcns.size} observations")

        val reportedTcns = TimeSource.Monotonic.measureTimedValue {
            // Compute the intersection of the two sets.
            candidateTcns.value.intersect(observedTcns)
        }

        assertEquals(reportedTcns.value, expectedReportedTcns)

        println("Took ${candidateTcns.duration} (expansion) + ${reportedTcns.duration} (comparison) = ${candidateTcns.duration + reportedTcns.duration} (total)")
    }

    @ExperimentalUnsignedTypes
    @Test
    fun basicReadWriteRoundTrip() {
        val rak = ReportAuthorizationKey(SecureRandom.getInstanceStrong())

        val rakBuf1 = rak.toByteArray()
        val rakBuf2 = ReportAuthorizationKey.fromByteArray(rakBuf1).toByteArray()
        assertArrayEquals(rakBuf1, rakBuf2)

        val tck = rak.initialTemporaryContactKey

        val tckBuf1 = tck.toByteArray()
        val tckBuf2 = TemporaryContactKey.fromByteArray(tckBuf1).toByteArray()
        assertArrayEquals(tckBuf1, tckBuf2)

        val signedReport = rak.createReport(
            MemoType.CoEpiV1,
            "symptom data".toByteArray(Charsets.UTF_8),
            20.toUShort(),
            100.toUShort()
        )

        val signedBuf1 = signedReport.toByteArray()
        val signedBuf2 = SignedReport.fromByteArray(signedBuf1).toByteArray()
        assertArrayEquals(signedBuf1, signedBuf2)

        val report = signedReport.verify()

        val reportBuf1 = report.toByteArray()
        val reportBuf2 = Report.fromByteArray(reportBuf1).toByteArray()
        assertArrayEquals(reportBuf1, reportBuf2)
    }
}