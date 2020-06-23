package org.coepi.android.core

import org.coepi.android.common.Result
import org.coepi.android.tcn.Tcn

interface ObservedTcnsRecorder {
    fun recordTcn(tcn: Tcn): Result<Unit, Throwable>
}

class ObservedTcnsRecorderImpl(private val nativeApi: NativeCore) : ObservedTcnsRecorder {
    override fun recordTcn(tcn: Tcn): Result<Unit, Throwable> =
        nativeApi.recordTcn(tcn.toHex()).asResult()
}
