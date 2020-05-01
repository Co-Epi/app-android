package org.tcncoalition.tcnclient.crypto

import cafe.cryptography.ed25519.Ed25519PrivateKey
import cafe.cryptography.ed25519.Ed25519PublicKey
import org.tcncoalition.tcnclient.TcnConstants.H_TCK_DOMAIN_SEPARATOR
import org.tcncoalition.tcnclient.TcnConstants.H_TCN_DOMAIN_SEPARATOR
import org.tcncoalition.tcnclient.TcnConstants.TCK_BYTES_LENGTH
import org.tcncoalition.tcnclient.TcnConstants.TCN_LENGTH
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.security.SecureRandom

/** Authorizes publication of a report of potential exposure. */
class ReportAuthorizationKey(internal val rak: Ed25519PrivateKey) : Writer {
    internal val rvk: Ed25519PublicKey = this.rak.derivePublic()

    constructor() : this(Ed25519PrivateKey.generate(SecureRandom()))

    /** Generates a new ReportAuthorizationKey. */
    constructor(random: SecureRandom) : this(Ed25519PrivateKey.generate(random))

    companion object : Reader<ReportAuthorizationKey> {
        /** Reads a [ReportAuthorizationKey] from [buf]. */
        override fun fromByteBuffer(buf: ByteBuffer): ReportAuthorizationKey {
            val rak = Ed25519PrivateKey.fromByteArray(buf.read32())
            return ReportAuthorizationKey(rak)
        }
    }

    /** Returns the size that this [ReportAuthorizationKey] will serialize into. */
    override fun sizeHint(): Int {
        return 32
    }

    /** Serializes a [ReportAuthorizationKey] into [buf]. */
    override fun toByteBuffer(buf: ByteBuffer) {
        buf.put(rak.toByteArray())
    }

    /** Serializes a [ReportAuthorizationKey] into a [ByteArray]. */
    override fun toByteArray(): ByteArray {
        return rak.toByteArray()
    }

    /** The initial temporary contact key derived from this report authorization key. */
    val initialTemporaryContactKey: TemporaryContactKey by lazy {
        tck0.ratchet()!!
    }

    /** This is internal because tck0 shouldn't be used to generate a tcn. */
    internal val tck0: TemporaryContactKey
        get() {
            val h = MessageDigest.getInstance("SHA-256")
            h.update(H_TCK_DOMAIN_SEPARATOR)
            h.update(rak.toByteArray())
            return TemporaryContactKey(KeyIndex(0), rvk, h.digest())
        }
}

/** A pseudorandom 128-bit value broadcast to nearby devices over Bluetooth. */
data class TemporaryContactNumber internal constructor(val bytes: ByteArray) {
    init {
        require(bytes.size == TCN_LENGTH) { "TCN must be $TCN_LENGTH bytes, was ${bytes.size}" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other as? TemporaryContactNumber == null) return false
        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}

/**
 * The index of a specific [TemporaryContactKey].
 *
 * Represents a value between zero and `2^16 - 1`.
 */
class KeyIndex(val short: Short) {
    @ExperimentalUnsignedTypes
    val uShort = short.toUShort()

    internal val bytes: ByteArray
        get() {
            val buf = ByteBuffer.allocate(2)
            buf.order(ByteOrder.LITTLE_ENDIAN)
            buf.putShort(short)
            return buf.array()
        }

    /** Returns `null` if the increment would cause the index to wrap around to zero. */
    fun checkedInc(): KeyIndex? {
        // We are representing the index internally as a Short, so we rely on
        // wrapping behaviour of Short.inc() to reach half of the index space.
        val nextIndex = short.inc()

        // If we arrive back at zero, we have wrapped the equivalent UShort.
        return if (nextIndex != 0.toShort()) {
            KeyIndex(nextIndex)
        } else {
            null
        }
    }

    internal fun dec() = KeyIndex(short.dec())
}

/** A ratcheting key used to derive temporary contact numbers. */
class TemporaryContactKey(
    val index: KeyIndex,
    val rvk: Ed25519PublicKey,
    val tckBytes: ByteArray
) : Writer {
    init {
        require(tckBytes.size == TCK_BYTES_LENGTH) { "tckBytes must be $TCK_BYTES_LENGTH bytes, was ${tckBytes.size}" }
    }

    companion object : Reader<TemporaryContactKey> {
        /**
         * Reads a [TemporaryContactKey] from [buf].
         *
         * The order of [buf] will be set to [ByteOrder.LITTLE_ENDIAN].
         */
        override fun fromByteBuffer(buf: ByteBuffer): TemporaryContactKey {
            buf.order(ByteOrder.LITTLE_ENDIAN)

            val index = KeyIndex(buf.short)
            val rvk = Ed25519PublicKey.fromByteArray(buf.read32())
            val tckBytes = buf.read32()

            return TemporaryContactKey(index, rvk, tckBytes)
        }
    }

    /** Returns the size that this [TemporaryContactKey] will serialize into. */
    override fun sizeHint(): Int {
        return 2 + 32 + TCK_BYTES_LENGTH
    }

    /**
     * Serializes a [TemporaryContactKey] into [buf].
     *
     * The order of [buf] will be set to [ByteOrder.LITTLE_ENDIAN].
     */
    override fun toByteBuffer(buf: ByteBuffer) {
        buf.order(ByteOrder.LITTLE_ENDIAN)
        buf.putShort(index.short)
        buf.put(rvk.toByteArray())
        buf.put(tckBytes)
    }

    private var ratcheted = false

    /** The temporary contact number derived from this key. */
    val temporaryContactNumber: TemporaryContactNumber by lazy {
        val h = MessageDigest.getInstance("SHA-256")
        h.update(H_TCN_DOMAIN_SEPARATOR)
        h.update(index.bytes)
        h.update(tckBytes)
        TemporaryContactNumber(h.digest().sliceArray(0 until TCN_LENGTH))
    }

    /**
     * Ratchets the key forward, producing a new key for a new temporary contact number, or `null`
     * if the report authorization key should be rotated.
     */
    fun ratchet(): TemporaryContactKey? {
        // Emulate a consuming method.
        if (ratcheted) throw IllegalStateException("key has already been ratcheted")
        ratcheted = true

        return index.checkedInc()?.let { nextIndex ->
            val h = MessageDigest.getInstance("SHA-256")
            h.update(H_TCK_DOMAIN_SEPARATOR)
            h.update(rvk.toByteArray())
            h.update(tckBytes)
            TemporaryContactKey(nextIndex, rvk, h.digest())
        }
    }
}
