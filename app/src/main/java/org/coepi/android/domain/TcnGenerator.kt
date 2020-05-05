package org.coepi.android.domain

import android.content.Context
import org.coepi.android.tcn.Tcn
import org.tcncoalition.tcnclient.TcnKeys

interface TcnGenerator {
    fun generateTcn(): Tcn
}

class TcnGeneratorImpl(context: Context) : TcnGenerator {
    private val tcnKeys: TcnKeys = TcnKeys(context)

    override fun generateTcn(): Tcn =
        Tcn(tcnKeys.generateTcn())
}
