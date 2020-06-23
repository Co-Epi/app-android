package org.coepi.android.api

import org.coepi.android.common.Result
import org.coepi.android.tcn.Tcn

interface ObservedTcnsRecorder {
    fun recordTcn(tcn: Tcn): Result<Unit, Throwable>
}

class ObservedTcnsRecorderImpl(private val nativeApi: NativeApi) : ObservedTcnsRecorder {
    override fun recordTcn(tcn: Tcn): Result<Unit, Throwable> =
        nativeApi.recordTcn(tcn.toHex()).asResult()
}
