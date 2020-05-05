package org.coepi.android.tcn

import org.coepi.android.domain.UnixTime

interface TcnDao {
    fun all(): List<ReceivedTcn>
    fun matchTcns(start: UnixTime, end: UnixTime, tcns: Array<String>): List<ReceivedTcn>
    fun findTcn(tcn: Tcn): ReceivedTcn?
    fun insert(tcn: ReceivedTcn): Boolean
}
