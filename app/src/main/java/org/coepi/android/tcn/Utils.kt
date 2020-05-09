package org.coepi.android.tcn

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.MessageDigest
import java.util.*

@ExperimentalUnsignedTypes
fun compareByteArray(a : UByteArray, b : UByteArray) : Int {
    for (i in 0..a.size) {
        if (a[i] != b[i]) {
            return a[i].toInt() - b[i].toInt()
        }
    }
    return 0
}

@ExperimentalUnsignedTypes
fun computeUUIDHash(selfUUID: UUID, otherUUID: UUID) : ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    val selfBytes = getBytesFromUUID(selfUUID).toUByteArray()
    val otherBytes = getBytesFromUUID(otherUUID).toUByteArray()
    var bytes: UByteArray
    if ( compareByteArray(selfBytes, otherBytes) < 0 ) {
        bytes = selfBytes + otherBytes
    } else {
        bytes = otherBytes + selfBytes
    }
    return md.digest(bytes.toByteArray()).toUByteArray().asByteArray()
}

// byte array expected in little endian format
@ExperimentalUnsignedTypes
fun UByteArray.toULong(): ULong {
    val reversed = reversed()

    var long: ULong = 0.toULong()
    for (index in 0 until reversed.size) {
        long = reversed[index].toULong().shl((reversed.count() - 1 - index) * 8) or long
    }
    return long
}

// byte array expected in little endian format
@ExperimentalUnsignedTypes
fun UByteArray.toUByte(): UByte = toULong().toUByte()

// byte array expected in little endian format
@ExperimentalUnsignedTypes
fun UByteArray.toUShort(): UShort = toULong().toUShort()

fun longToByteArray(i: Long): ByteArray? {
    val bb: ByteBuffer = ByteBuffer.allocate(Long.SIZE_BYTES)
    bb.order(ByteOrder.LITTLE_ENDIAN)
    bb.putLong(i)
    return bb.array()
}

fun byteArrayToInt(b: ByteArray?): Int {
    val bb: ByteBuffer = ByteBuffer.wrap(b)
    bb.order(ByteOrder.BIG_ENDIAN)
    return bb.getInt()
}


fun getBytesFromUUID(uuid: UUID): ByteArray {
    val bb = ByteBuffer.wrap(ByteArray(16))
    bb.putLong(uuid.mostSignificantBits)
    bb.putLong(uuid.leastSignificantBits)
    return bb.array()
}

fun getUUIDFromBytes(bytes: ByteArray): UUID {
    val byteBuffer = ByteBuffer.wrap(bytes)
    val high = byteBuffer.long
    val low = byteBuffer.long
    return UUID(high, low)
}

fun getDateStamp(timeStamp : Long) : String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
    val date = Date( timeStamp * 1000L)
    return  sdf.format(date)
}


class CommonHash {
    constructor()

    constructor(hexs : String) {
        val s = hexs.toCharArray()
        val len = s.size
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
    }

    var bytes = ByteArray(32)

    override fun toString(): String {
        var out = ""
        for (j in bytes.indices) {
            out += String.format("%02x", bytes[j])
        }
        return out

    }
}

fun computeHash(s : String) : CommonHash {
    val h = CommonHash()
    val bytes = s.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")

    val digest = md.digest(bytes).toUByteArray().asByteArray()

    h.bytes = digest
    return h
}

