package org.coepi.android.tcn

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.PublishSubject.create
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.coepi.android.api.TcnApi
import org.coepi.android.api.publicreport.PublicReport
import org.coepi.android.api.publicreport.PublicReportMapper
import org.coepi.android.common.ApiReportMapper
import org.coepi.android.common.Result
import org.coepi.android.common.Result.Success
import org.coepi.android.common.doIfError
import org.coepi.android.common.doIfSuccess
import org.coepi.android.common.flatMap
import org.coepi.android.domain.UnixTime.Companion.now
import org.coepi.android.domain.symptomflow.SymptomInputs
import org.coepi.android.extensions.retrofit.executeSafe
import org.coepi.android.system.log.log
import org.coepi.android.system.rx.OperationState
import org.coepi.android.system.rx.OperationState.NotStarted
import org.coepi.android.system.rx.OperationState.Progress
import org.coepi.android.system.rx.VoidOperationState

interface TcnReportRepo {
    val alerts: Observable<List<Alert>>

    val sendState: Observable<VoidOperationState>

    fun delete(report: Alert)

    fun submitReport(inputs: SymptomInputs): Result<Unit, Throwable>
}

@ExperimentalUnsignedTypes
class TcnReportRepoImpl(
    private val tcnReportDao: TcnReportDao,
    private val apiReportMapper: ApiReportMapper,
    private val api: TcnApi,
    private val publicReportMapper: PublicReportMapper
) : TcnReportRepo {

    override val sendState: PublishSubject<VoidOperationState> = create()

    override val alerts: Observable<List<Alert>> = tcnReportDao.rawAlerts.map { rawAlerts ->
        rawAlerts.mapNotNull { rawAlert ->
            apiReportMapper.fromRawAlert(rawAlert).also { alert ->
                if (alert == null) {
                    log.e("Couldn't raw alert: $alert. Skipped.")
                }
            }
        }
    }

    override fun submitReport(inputs: SymptomInputs): Result<Unit, Throwable> {
        val publicReport = publicReportMapper.toPublicReport(inputs, now())
        return if (publicReport != null) {
            sendReport(publicReport)
        } else {
            log.d("Nothing to send.")
            Success(Unit)
        }
    }

    private fun sendReport(report: PublicReport): Result<Unit, Throwable> {
        sendState.onNext(Progress)

        return apiReportMapper.toApiReport(report).let { apiReport ->
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

    override fun delete(report: Alert) {
        tcnReportDao.delete(report)
    }
}