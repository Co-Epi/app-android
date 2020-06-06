package org.coepi.android.api.memo

import org.coepi.android.api.memo.mapper.BitMapper
import org.coepi.android.api.memo.mapper.BooleanMapper
import org.coepi.android.api.memo.mapper.CoughSeverityMapper
import org.coepi.android.api.memo.mapper.FeverSeverityMapper
import org.coepi.android.api.memo.mapper.TimeMapper
import org.coepi.android.api.memo.mapper.TimeUserInputMapper
import org.coepi.android.api.memo.mapper.VersionMapper
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime

@ExperimentalUnsignedTypes
interface MemoMapper {
    fun toMemo(report: PublicReport, time: UnixTime): Memo
    fun toReport(memo: Memo): PublicReport
}

/**
 * 16 bit memo version
 * 64 bit date
 * 1 bit "I don't have symptoms today"
 * 1 bit "I have symptoms not in the list"
 * 1 bit cough
 * 1 bit breathlessness
 * 1 bit fever
 *
 * NOTE: For now direct mapping SymptomInputs <-> Memo
 * When the spec is clearer, we should introduce an intermediate type that represents memo data
 * so we have SymptomInputs -> Intermediate type <-> Memo
 */
@ExperimentalUnsignedTypes
class MemoMapperImpl: MemoMapper {

    private val versionMapper = VersionMapper()
    private val timeMapper = TimeMapper()
    private val timeUserInputMapper = TimeUserInputMapper()
    private val booleanMapper = BooleanMapper()
    private val coughSeverityMapper = CoughSeverityMapper()
    private val feverSeverityMapper = FeverSeverityMapper()

    override fun toMemo(report: PublicReport, time: UnixTime): Memo {
        val memoVersion: UShort = 1.toUShort()

        val bits: List<BitList> = listOf(
            versionMapper.toBits(memoVersion),
            timeMapper.toBits(time),
            timeUserInputMapper.toBits(report.earliestSymptomTime),
            coughSeverityMapper.toBits(report.coughSeverity),
            feverSeverityMapper.toBits(report.feverSeverity),
            booleanMapper.toBits(report.breathlessness)
        )

        return bits.fold(BitList(emptyList())) { acc, e ->
            val res = acc.concat(e)
            res
        }.toUByteArray().let { Memo(it) }
    }

    override fun toReport(memo: Memo): PublicReport {
        val bitArray: List<Boolean> = memo.bytes.flatMap {
            it.toBits().bits
        }

        var next = 0

        // Version for now not handled
        val versionResult = extract(bitArray, versionMapper, next).value { next += it }

        val timeResult = extract(bitArray, timeMapper, next).value { next += it }

        val earliestSymptomTime = extract(bitArray, timeUserInputMapper, next).value { next += it }
        val coughSeverity = extract(bitArray, coughSeverityMapper, next).value { next += it }
        val feverSeverity = extract(bitArray, feverSeverityMapper, next).value { next += it }
        val breathlessness = extract(bitArray, booleanMapper, next).value { next += it }

        return PublicReport(
            reportTime = timeResult,
            earliestSymptomTime = earliestSymptomTime,
            feverSeverity = feverSeverity,
            coughSeverity = coughSeverity,
            breathlessness = breathlessness
        )
    }

    private data class ExtractResult<T>(val value: T, val count: Int) {
        // Convenience to parse memo with less boilerplate
        fun value(f: (Int) -> Unit): T {
            f(count)
            return value
        }
    }

    @ExperimentalUnsignedTypes
    private fun <T> extract(bits: List<Boolean>, mapper: BitMapper<T>, start: Int): ExtractResult<T> =
        ExtractResult(mapper.fromBits(BitList(bits.subList(start, mapper.bitCount + start))), mapper.bitCount)
}

@ExperimentalUnsignedTypes
fun UShort.toBits(): BitList = toULong().toBits(16)

@ExperimentalUnsignedTypes
fun UByte.toBits(): BitList = toULong().toBits(8)

@ExperimentalUnsignedTypes
fun ULong.toBits(): BitList = toBits(64)

@ExperimentalUnsignedTypes
fun ULong.toBits(size: Int): BitList =
    (0 until size).map { index ->
        val value = this shr index and 0x01.toULong()
        value == 1.toULong()
    }.let { BitList(it) }

@ExperimentalUnsignedTypes
fun Boolean.toBits(): BitList = BitList(listOf(this))
