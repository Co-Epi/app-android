package org.tcncoalition.tcnclient.crypto

/** An unknown memo type was encountered while parsing a report. */
class UnknownMemoType(private val t: Byte) : Exception("Unknown memo type $t")

/** Reports cannot include the TCN with index 0. */
class InvalidReportIndex : Exception("Invalid TCN index in report")

/** An oversize memo field was supplied when creating a report. */
class OversizeMemo(private val len: Int) : Exception("Oversize memo field: $len bytes")

/** A report failed the source integrity check. */
class ReportVerificationFailed : Exception("Report verification failed")
