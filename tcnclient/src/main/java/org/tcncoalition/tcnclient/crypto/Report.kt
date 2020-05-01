package org.tcncoalition.tcnclient.crypto

import cafe.cryptography.ed25519.Ed25519PublicKey
import cafe.cryptography.ed25519.Ed25519Signature
import org.tcncoalition.tcnclient.TcnConstants.TCK_BYTES_LENGTH
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** Describes the intended type of the contents of a memo field. */
enum class MemoType(internal val t: Byte) {
    /** The CoEpi symptom self-report format, version 1 (TBD) */
    CoEpiV1(0),

    /** The CovidWatch test data format, version 1 (TBD) */
    CovidWatchV1(1),

    /** Reserved for future use. */
    Reserved(-1);

    companion object : Reader<MemoType> {
        /** Converts a byte into a [MemoType]. */
        override fun fromByteBuffer(buf: ByteBuffer): MemoType {
            val t = buf.get()
            return when (t.toInt()) {
                0 -> CoEpiV1
                1 -> CovidWatchV1
                else -> throw UnknownMemoType(t)
            }
        }
    }
}

/** A report of potential exposure. */
class Report(
    val rvk: Ed25519PublicKey,
    val tckBytes: ByteArray,
    val j1: KeyIndex,
    val j2: KeyIndex,
    val memoType: MemoType,
    val memoData: ByteArray
) : Writer {
    init {
        require(tckBytes.size == TCK_BYTES_LENGTH) { "tckBytes must be $TCK_BYTES_LENGTH bytes, was ${tckBytes.size}" }
        if (j1.short == 0.toShort()) throw InvalidReportIndex()
    }

    companion object : Reader<Report> {
        /**
         * Reads a [Report] from [buf].
         *
         * The order of [buf] will be set to [ByteOrder.LITTLE_ENDIAN].
         */
        override fun fromByteBuffer(buf: ByteBuffer): Report {
            buf.order(ByteOrder.LITTLE_ENDIAN)

            val rvk = Ed25519PublicKey.fromByteArray(buf.read32())
            val tckBytes = buf.read32()
            val j1 = KeyIndex(buf.short)
            val j2 = KeyIndex(buf.short)
            val memoType = MemoType.fromByteBuffer(buf)
            val memoData = buf.readCompactVec()

            return Report(rvk, tckBytes, j1, j2, memoType, memoData)
        }
    }

    /** Returns the size that this [Report] will serialize into. */
    override fun sizeHint(): Int {
        return 32 + TCK_BYTES_LENGTH + 2 + 2 + 1 + 1 + memoData.size
    }

    /**
     * Serializes a [Report] into [buf].
     *
     * The order of [buf] will be set to [ByteOrder.LITTLE_ENDIAN].
     */
    override fun toByteBuffer(buf: ByteBuffer) {
        val memoLen = memoData.size.toByte()
        if (memoLen.toInt() != memoData.size) throw OversizeMemo(memoData.size)

        buf.order(ByteOrder.LITTLE_ENDIAN)
        buf.put(rvk.toByteArray())
        buf.put(tckBytes)
        buf.putShort(j1.short)
        buf.putShort(j2.short)
        buf.put(memoType.t)
        buf.put(memoLen)
        buf.put(memoData)
    }

    class TemporaryContactNumberIterator(
        private var tck: TemporaryContactKey,
        private val end: KeyIndex
    ) :
        Iterator<TemporaryContactNumber> {
        @ExperimentalUnsignedTypes
        override fun hasNext(): Boolean {
            return tck.index.uShort < end.uShort
        }

        override fun next(): TemporaryContactNumber {
            val tcn = tck.temporaryContactNumber
            tck = tck.ratchet()!! // We do not ratchet past end <= UShort.MAX_VALUE.
            return tcn
        }
    }

    /** An iterator over all temporary contact numbers included in the report. */
    val temporaryContactNumbers: Iterator<TemporaryContactNumber>
        get() = TemporaryContactNumberIterator(
            TemporaryContactKey(
                j1.dec(),
                rvk,
                tckBytes
            ).ratchet()!!, // j1 - 1 < j1 <= UShort.MAX_VALUE
            j2
        )
}


/**
 * Creates a report of potential exposure.
 *
 * # Inputs
 *
 * - [memoType], [memoData]: the type and data for the report's memo field.
 * - `[j1] > 0`: the ratchet index of the first temporary contact number in the report.
 * - [j2]: the ratchet index of the last temporary contact number other users should check.
 *
 * # Notes
 *
 * Creating a report reveals *all* temporary contact numbers subsequent to [j1],
 * not just up to [j2], which is included for convenience.
 *
 * The [memoData] must be less than 256 bytes long.
 *
 * Reports are unlinkable from each other **only up to the memo field**. In
 * other words, adding the same high-entropy data to the memo fields of multiple
 * reports will cause them to be linkable.
 */
@ExperimentalUnsignedTypes
fun ReportAuthorizationKey.createReport(
    memoType: MemoType,
    memoData: ByteArray,
    j1: UShort,
    j2: UShort
): SignedReport {
    // Ensure that j1 is at least 1.
    val j1Coerced = if (j1 == 0.toUShort()) {
        1.toUShort()
    } else {
        j1
    }

    // Recompute tck_{j1 - 1}. This requires recomputing j1 - 1 hashes, but
    // creating reports is done infrequently and it means we don't force the
    // caller to have saved all intermediate hashes.
    var tck = tck0
    for (i in 0 until j1Coerced.toInt() - 1) {
        tck = tck.ratchet()!!
    }

    val report = Report(
        rvk,
        tck.tckBytes,
        KeyIndex(j1Coerced.toShort()),
        KeyIndex(j2.toShort()),
        memoType,
        memoData
    )

    return SignedReport(report, rak.expand().sign(report.toByteArray(), rvk))
}

class SignedReport(val report: Report, val signature: Ed25519Signature) : Writer {
    companion object : Reader<SignedReport> {
        /**
         * Reads a [SignedReport] from [buf].
         *
         * The order of [buf] will be set to [ByteOrder.LITTLE_ENDIAN].
         */
        override fun fromByteBuffer(buf: ByteBuffer): SignedReport {
            val report = Report.fromByteBuffer(buf)
            val signature = Ed25519Signature.fromByteArray(buf.read64())
            return SignedReport(report, signature)
        }
    }

    /** Returns the size that this [SignedReport] will serialize into. */
    override fun sizeHint(): Int {
        return report.sizeHint() + 64
    }

    /**
     * Serializes a [SignedReport] into [buf].
     *
     * The order of [buf] will be set to [ByteOrder.LITTLE_ENDIAN].
     */
    override fun toByteBuffer(buf: ByteBuffer) {
        buf.put(report.toByteArray())
        buf.put(signature.toByteArray())
    }

    /**
     * Verifies the source integrity of this report, producing a [Report] if successful.
     *
     * Throws [ReportVerificationFailed] if the [SignedReport] is invalid.
     */
    fun verify(): Report {
        if (report.rvk.verify(report.toByteArray(), signature)) {
            return report
        } else {
            throw ReportVerificationFailed()
        }
    }
}
