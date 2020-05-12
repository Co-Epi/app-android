package org.coepi.android.common

import android.content.Context
import org.coepi.android.api.memo.Memo
import org.coepi.android.api.memo.MemoMapper
import org.coepi.android.domain.UnixTime
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.domain.symptomflow.SymptomInputs
import org.coepi.android.extensions.base64ToByteArray
import org.coepi.android.extensions.toBase64String
import org.coepi.android.system.Resources
import org.coepi.android.tcn.SymptomReport
import org.coepi.android.tcn.TcnReport
import org.tcncoalition.tcnclient.TcnKeys
import org.tcncoalition.tcnclient.crypto.MemoType

interface ApiSymptomsMapper {
    fun toApiReport(inputs: SymptomInputs): String
    fun fromTcnReport(report: TcnReport): SymptomReport?
}

class ApiSymptomsMapperImpl(
    context: Context,
    private val resources: Resources,
    private val memoMapper: MemoMapper
) : ApiSymptomsMapper {
    private val tcnKeys: TcnKeys = TcnKeys(context)

    override fun toApiReport(inputs: SymptomInputs): String =
        tcnKeys.createReport(
            // TODO modify TCN lib to use unsigned bytes?
            memoMapper.toMemo(inputs, now()).bytes.asByteArray(),
            MemoType.CoEpiV1
        ).toByteArray().toBase64String()

    override fun fromTcnReport(report: TcnReport): SymptomReport? =
        report.memoStr.base64ToByteArray()?.toUByteArray()?.let { memoBytes ->
            SymptomReport(
                id = report.id,
                inputs = memoMapper.toInputs(Memo(memoBytes)),
                timestamp = UnixTime.fromValue(report.timestamp)
            )
        }
}
