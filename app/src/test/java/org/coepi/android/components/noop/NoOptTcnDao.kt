package org.coepi.android.components.noop

import org.coepi.android.domain.UnixTime
import org.coepi.android.tcn.ReceivedTcn
import org.coepi.android.tcn.Tcn
import org.coepi.android.tcn.TcnDao

class NoOptTcnDao: TcnDao {
    override fun all(): List<ReceivedTcn> = emptyList()

    override fun matchTcns(
        start: UnixTime,
        end: UnixTime,
        tcns: Array<String>
    ): List<ReceivedTcn> = emptyList()

    override fun findTcn(tcn: Tcn): ReceivedTcn? = null

    override fun insert(tcn: ReceivedTcn): Boolean = false
}
