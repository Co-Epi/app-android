package org.coepi.android.tcn

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.coepi.android.api.TcnApi
import org.coepi.android.common.ApiSymptomsMapper
import org.coepi.android.api.memo.MemoMapper
import org.coepi.android.common.Result
import org.coepi.android.common.Result.Success
import org.coepi.android.common.doIfError
import org.coepi.android.common.doIfSuccess
import org.coepi.android.common.flatMap
import org.coepi.android.domain.symptomflow.SymptomInputs
import org.coepi.android.extensions.retrofit.executeSafe
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.VoidOperationState
import java.util.UUID

interface TcnReportRepo {
    val reports: Observable<List<SymptomReport>>

    val sendState: Observable<VoidOperationState>

    fun delete(report: SymptomReport)

    fun submitReport(inputs: SymptomInputs): Result<Unit, Throwable>
}

class TcnReportRepoImpl(
    private val tcnReportDao: TcnReportDao,
    private val symptomsProcessor: ApiSymptomsMapper,
    private val api: TcnApi
) : TcnReportRepo {

    override val sendState: PublishSubject<VoidOperationState> = create()

    override val reports: Observable<List<SymptomReport>> = tcnReportDao.reports.map { reports ->
        reports.mapNotNull { report ->
            symptomsProcessor.fromTcnReport(report.report).also { symptomReport ->
                if (symptomReport == null) {
                    log.e("Couldn't parse report: $symptomReport. Skipped.")
                }
            }
        }
    }

    override fun submitReport(inputs: SymptomInputs): Result<Unit, Throwable> {
        sendState.onNext(Progress)

        return symptomsProcessor.toApiReport(inputs).let { apiReport ->
            // NOTE: Needs to be sent as text/plain to not add quotes
            val requestBody = apiReport.toRequestBody("text/plain".toMediaType())
            api.postReport(requestBody).executeSafe().flatMap {
                Success(Unit)
            }.doIfError {
                log.e("Error posting report: ${it.message}")
            }

        // TODO put in Result extension
        }.doIfSuccess {
            sendState.onNext(OperationState.Success(Unit))
        }.doIfError {
            sendState.onNext(OperationState.Failure(it))
        }.also {
            sendState.onNext(NotStarted)
        }
    }

    override fun delete(report: SymptomReport) {
        tcnReportDao.delete(report)
    }
}
