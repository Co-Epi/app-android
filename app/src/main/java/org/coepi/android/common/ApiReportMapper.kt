package org.coepi.android.common

import android.content.Context
import org.coepi.android.api.memo.Memo
import org.coepi.android.api.memo.MemoMapper
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.extensions.base64ToByteArray
import org.coepi.android.extensions.toBase64String
import org.coepi.android.tcn.Alert
import org.coepi.android.tcn.RawAlert
import org.tcncoalition.tcnclient.TcnKeys
import org.tcncoalition.tcnclient.crypto.MemoType

interface ApiReportMapper {
    fun toApiReport(report: PublicReport): String
    fun fromRawAlert(report: RawAlert): Alert?
}

@ExperimentalUnsignedTypes
class ApiSymptomsMapperImpl(
    context: Context,
    private val memoMapper: MemoMapper
) : ApiReportMapper {
    private val tcnKeys: TcnKeys = TcnKeys(context)

    override fun toApiReport(report: PublicReport): String =
        tcnKeys.createReport(
            // TODO modify TCN lib to use unsigned bytes?
            memoMapper.toMemo(report, now()).bytes.asByteArray(),
            MemoType.CoEpiV1
        ).toByteArray().toBase64String()

    override fun fromRawAlert(report: RawAlert): Alert? =
        report.memoStr.base64ToByteArray()?.toUByteArray()?.let { memoBytes ->
            Alert(
                id = report.id,
                report = memoMapper.toReport(Memo(memoBytes)),
                contactTime = report.contactTime
            )
        }
}
