package org.coepi.android.domain

import android.content.Context
import org.coepi.android.cen.Cen
import org.tcncoalition.tcnclient.TcnKeys

interface TcnGenerator {
    fun generateTcn(): Cen
}

class TcnGeneratorImpl(context: Context) : TcnGenerator {
    private val tcnKeys: TcnKeys = TcnKeys(context)

    override fun generateTcn(): Cen =
        Cen(tcnKeys.generateTcn())
}
