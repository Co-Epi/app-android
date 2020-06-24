package org.coepi.android.core

import org.coepi.android.extensions.hexToByteArray
import org.coepi.android.tcn.Tcn

interface TcnGenerator {
    fun generateTcn(): Tcn
}

class TcnGeneratorImpl(private val nativeApi: NativeCore) : TcnGenerator {
    override fun generateTcn(): Tcn =
        Tcn(nativeApi.generateTcn().hexToByteArray())
}
