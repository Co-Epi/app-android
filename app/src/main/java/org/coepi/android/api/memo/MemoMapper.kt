package org.coepi.android.api.memo

import org.coepi.android.api.memo.mapper.BitMapper
import org.coepi.android.api.memo.mapper.BooleanMapper
import org.coepi.android.api.memo.mapper.TimeMapper
import org.coepi.android.api.memo.mapper.VersionMapper
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.symptomflow.SymptomId.BREATHLESSNESS
import org.coepi.android.domain.symptomflow.SymptomId.COUGH
import org.coepi.android.domain.symptomflow.SymptomId.EARLIESTSYMPTOM
import org.coepi.android.domain.symptomflow.SymptomId.FEVER
import org.coepi.android.domain.symptomflow.SymptomId.NONE
import org.coepi.android.domain.symptomflow.SymptomId.OTHER
import org.coepi.android.domain.symptomflow.SymptomInputs

@ExperimentalUnsignedTypes
interface MemoMapper {
    fun toMemo(inputs: SymptomInputs, time: UnixTime): Memo
    fun toInputs(memo: Memo): SymptomInputs
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
    private val booleanMapper = BooleanMapper()

    override fun toMemo(inputs: SymptomInputs, time: UnixTime): Memo {
        val memoVersion: UShort = 1.toUShort()
        val noSymptomsToday: Boolean = inputs.ids.contains(NONE)
        val otherSymptoms: Boolean = inputs.ids.contains(OTHER)
        val hasCough: Boolean = inputs.ids.contains(COUGH)
        val hasFever: Boolean = inputs.ids.contains(FEVER)
        val hasBreathlessness: Boolean = inputs.ids.contains(BREATHLESSNESS)
        val hasEarliestSymptom: Boolean = inputs.ids.contains(EARLIESTSYMPTOM)

        val bits: List<BitList> = listOf(
            versionMapper.toBits(memoVersion),
            timeMapper.toBits(time),
            booleanMapper.toBits(noSymptomsToday),
            booleanMapper.toBits(otherSymptoms),
            booleanMapper.toBits(hasCough),
            booleanMapper.toBits(hasBreathlessness),
            booleanMapper.toBits(hasFever),
            booleanMapper.toBits(hasEarliestSymptom)
        )

        return bits.fold(BitList(emptyList())) { acc, e ->
            val res = acc.concat(e)
            res
        }.toUByteArray().let { Memo(it) }
    }

    override fun toInputs(memo: Memo): SymptomInputs {
        val bitArray: List<Boolean> = memo.bytes.flatMap {
            it.toBits().bits
        }

        var next = 0

        // Version for now not handled
        val versionResult = extract(bitArray, versionMapper, next).value { next += it }

        // TODO handle time
        val timeResult = extract(bitArray, timeMapper, next).value { next += it }

        val noSymptomsToday = extract(bitArray, booleanMapper, next).value { next += it }
        val symptomsNotInList = extract(bitArray, booleanMapper, next).value { next += it }
        val hasCough = extract(bitArray, booleanMapper, next).value { next += it }
        val hasBreathlessness = extract(bitArray, booleanMapper, next).value { next += it }
        val hasFever = extract(bitArray, booleanMapper, next).value { next += it }
        val hasEarliestSymptom = extract(bitArray, booleanMapper, next).value { next += it}

        return SymptomInputs(
            ids = listOfNotNull(
                noSymptomsToday.takeIf { it }?.let { NONE },
                symptomsNotInList.takeIf { it }?.let { OTHER },
                hasCough.takeIf { it }?.let { COUGH },
                hasBreathlessness.takeIf { it }?.let { BREATHLESSNESS },
                hasFever.takeIf { it }?.let { FEVER },
                hasEarliestSymptom.takeIf { it }?.let {EARLIESTSYMPTOM}
            ).toSet()
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
