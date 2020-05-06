package org.coepi.android

import com.google.common.truth.Truth.assertThat
import org.coepi.android.common.isSuccess
import org.coepi.android.common.successOrNull
import org.coepi.android.components.PreferencesReturningObject
import org.coepi.android.components.TcnApiReturningReports
import org.coepi.android.components.TcnDaoReturningAll
import org.coepi.android.components.noop.NoOpNewAlertsNotificationShower
import org.coepi.android.components.noop.NoOpPreferences
import org.coepi.android.components.noop.NoOpTcnApi
import org.coepi.android.components.noop.NoOpTcnMatcher
import org.coepi.android.components.noop.NoOpTcnReportsDao
import org.coepi.android.components.noop.NoOptTcnDao
import org.coepi.android.domain.TcnMatcherImpl
import org.coepi.android.domain.UnixTime
import org.coepi.android.extensions.base64ToByteArray
import org.coepi.android.repo.reportsupdate.ReportsInterval
import org.coepi.android.repo.reportsupdate.ReportsUpdaterImpl
import org.coepi.android.tcn.ReceivedTcn
import org.coepi.android.tcn.Tcn
import org.junit.Test
import org.tcncoalition.tcnclient.crypto.SignedReport

class ReportsUpdaterImplUnitTests {
    // A fixed "now" for replicable tests
    private val now: UnixTime = UnixTime.fromValue(1588779297)

    @Test
    fun retrieves_reports() {
        val apiReportStrings = listOf(
            "rSqWpM3ZQm7hfQ3q2x2llnFHiNhyRrUQPKEtJ33VKQcwT7Ly6e4KGaj5ZzjWt0m4c0v5n/VH5HO9UXbPXvsQTgEAQQAALFVtMVdNbHBZU1hOSlJYaDJZek5OWjJJeVdXZFpXRUozV2xoU2NHUkhWVDA9jn0pZAeME6ZBRHJOlfIikyfS0Pjg6l0txhhz6hz4exTxv8ryA3/Z26OebSRwzRfRgLdWBfohaOwOcSaynKqVCg==",
            "LbSUvv320gtY2qTZbumxno7KJ/BDWnHuHcUH0fNv144p+K1xbPt+YQuxFHzFfo71HoegSspNJLaAz93InuQHHQEAAQAACFJtVjJaWEk9iXj1FGy+r4cmNrS84AzHzx5wS0FZJXzXFFfvqwAogt6qjIe7+6CIJ8mrFrCen3nAVrQo3Bd1jsGe6UjybRUlAA=="
        )

        val reportsUpdater = ReportsUpdaterImpl(
            NoOpTcnMatcher(),
            TcnApiReturningReports(apiReportStrings), NoOptTcnDao(),
            NoOpTcnReportsDao(), NoOpPreferences(), NoOpNewAlertsNotificationShower()
        )

        val interval = ReportsInterval(0, 20)

        val reportsResult = reportsUpdater.retrieveReports(interval)

        assertThat(reportsResult.isSuccess())

        val chunk = reportsResult.successOrNull()!!
        assertThat(chunk.interval).isEqualTo(interval)
        assertThat(chunk.reports.size).isEqualTo(2)
        assertThat(chunk.reports[0].signature).isEqualTo(toSignedReport(apiReportStrings[0])?.signature)
        assertThat(chunk.reports[1].signature).isEqualTo(toSignedReport(apiReportStrings[1])?.signature)
    }

    @Test
    fun find_match() {
        val reports = listOf(
            "rSqWpM3ZQm7hfQ3q2x2llnFHiNhyRrUQPKEtJ33VKQcwT7Ly6e4KGaj5ZzjWt0m4c0v5n/VH5HO9UXbPXvsQTgEAQQAALFVtMVdNbHBZU1hOSlJYaDJZek5OWjJJeVdXZFpXRUozV2xoU2NHUkhWVDA9jn0pZAeME6ZBRHJOlfIikyfS0Pjg6l0txhhz6hz4exTxv8ryA3/Z26OebSRwzRfRgLdWBfohaOwOcSaynKqVCg==",
            "LbSUvv320gtY2qTZbumxno7KJ/BDWnHuHcUH0fNv144p+K1xbPt+YQuxFHzFfo71HoegSspNJLaAz93InuQHHQEAAQAACFJtVjJaWEk9iXj1FGy+r4cmNrS84AzHzx5wS0FZJXzXFFfvqwAogt6qjIe7+6CIJ8mrFrCen3nAVrQo3Bd1jsGe6UjybRUlAA=="
        ).map {
            toSignedReport(it)!!
        }

        val reportsUpdater = ReportsUpdaterImpl(
            TcnMatcherImpl(), NoOpTcnApi(),
            TcnDaoReturningAll(
                // We have a TCN stored belonging to the first report
                listOf(ReceivedTcn(reports.first().generateFirstTcn()!!, now))
            ),
            NoOpTcnReportsDao(), NoOpPreferences(), NoOpNewAlertsNotificationShower()
        )

        val matches = reportsUpdater.findMatches(reports)

        assertThat(matches.count()).isEqualTo(1)
        assertThat(matches[0].signature).isEqualTo(reports[0].signature)
    }

    @Test
    fun start_interval_contains_time_if_no_last_interval_stored() {
        val reportsUpdater = ReportsUpdaterImpl(
            NoOpTcnMatcher(), NoOpTcnApi(), NoOptTcnDao(), NoOpTcnReportsDao(),
            PreferencesReturningObject(null), NoOpNewAlertsNotificationShower()
        )
        val interval = reportsUpdater.determineStartInterval(now)

        assertThat(interval.contains(now)).isTrue()
    }

    @Test
    fun start_interval_uses_interval_after_stored() {
        val storedInterval = ReportsInterval(1, 10)

        val reportsUpdater = ReportsUpdaterImpl(
            NoOpTcnMatcher(), NoOpTcnApi(), NoOptTcnDao(), NoOpTcnReportsDao(),
            PreferencesReturningObject(
                storedInterval
            ), NoOpNewAlertsNotificationShower()
        )

        val interval = reportsUpdater.determineStartInterval(now)
        assertThat(interval).isEqualTo(storedInterval.next())
    }

    @Test
    fun interval_sequence_is_empty_if_starts_same_time_as_until() {
        val reportsUpdater = ReportsUpdaterImpl(
            NoOpTcnMatcher(), NoOpTcnApi(), NoOptTcnDao(), NoOpTcnReportsDao(), NoOpPreferences(),
            NoOpNewAlertsNotificationShower()
        )

        val from = ReportsInterval(0, 20)
        val until = UnixTime.fromValue(0)
        val intervals = reportsUpdater.generateIntervalsSequence(from, until).toList()

        // from doesn't start before until, so sequence is empty
        assertThat(intervals.size).isEqualTo(0)
    }

    @Test
    fun interval_sequence_is_empty_if_starts_starts_before_until() {
        val reportsUpdater = ReportsUpdaterImpl(
            NoOpTcnMatcher(), NoOpTcnApi(), NoOptTcnDao(), NoOpTcnReportsDao(), NoOpPreferences(),
            NoOpNewAlertsNotificationShower()
        )

        val from = ReportsInterval(20, 20)
        val until = UnixTime.fromValue(10)
        val intervals = reportsUpdater.generateIntervalsSequence(from, until).toList()

        // from doesn't start before until, so sequence is empty
        assertThat(intervals.size).isEqualTo(0)
    }

    @Test
    fun interval_sequence_has_one_element_with_until() {
        val reportsUpdater = ReportsUpdaterImpl(
            NoOpTcnMatcher(), NoOpTcnApi(), NoOptTcnDao(), NoOpTcnReportsDao(), NoOpPreferences(),
            NoOpNewAlertsNotificationShower()
        )

        val from = ReportsInterval(0, 20)
        val until = UnixTime.fromValue(1)
        val intervals = reportsUpdater.generateIntervalsSequence(from, until).toList()

        assertThat(intervals.size).isEqualTo(1)
    }

    @Test
    fun interval_sequence_has_two_intervals_including_until() {
        val reportsUpdater = ReportsUpdaterImpl(
            NoOpTcnMatcher(), NoOpTcnApi(), NoOptTcnDao(), NoOpTcnReportsDao(), NoOpPreferences(),
            NoOpNewAlertsNotificationShower()
        )

        val from = ReportsInterval(0, 20)
        val until = UnixTime.fromValue(21)
        val intervals = reportsUpdater.generateIntervalsSequence(from, until).toList()

        assertThat(intervals.size).isEqualTo(2)
        assertThat(intervals[0]).isEqualTo(from)
        assertThat(intervals[1]).isEqualTo(ReportsInterval(1, 20))
    }

    @Test
    fun interval_sequence_has_multiple_intervals_including_until() {
        val reportsUpdater = ReportsUpdaterImpl(
            NoOpTcnMatcher(), NoOpTcnApi(), NoOptTcnDao(), NoOpTcnReportsDao(), NoOpPreferences(),
            NoOpNewAlertsNotificationShower()
        )

        val from = ReportsInterval(1234, 20)
        val until = UnixTime.fromValue(12345678)
        val intervals = reportsUpdater.generateIntervalsSequence(from, until).toList()

        assertThat(intervals.size).isEqualTo((until.value - from.start) / from.length + 1)
        assertThat(intervals.first()).isEqualTo(from)
        assertThat(intervals.last()).isEqualTo(ReportsInterval(until.value / from.length,
            from.length))
    }

    // TODO centralize, maybe create Reports wrapper to hide the TCN lib implementation
    private fun toSignedReport(reportString: String): SignedReport? =
        reportString.base64ToByteArray()?.let { SignedReport.fromByteArray(it) }

    private fun SignedReport.generateFirstTcn(): Tcn? =
        report.temporaryContactNumbers.let {
            if (it.hasNext()) { it.next() } else { null }
        }?.let { Tcn(it.bytes) }
}
